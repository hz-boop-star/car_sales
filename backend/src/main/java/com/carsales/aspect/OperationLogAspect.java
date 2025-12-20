package com.carsales.aspect;

import com.carsales.annotation.OperationLog;
import com.carsales.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 操作日志切面
 * 记录关键操作日志
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final JwtUtil jwtUtil;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Around("@annotation(com.carsales.annotation.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        OperationLog operationLog = signature.getMethod().getAnnotation(OperationLog.class);
        String operation = operationLog.value();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String username = "匿名";
        String ip = "未知";

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            ip = getIpAddress(request);

            // 尝试从 Token 获取用户名
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                try {
                    username = jwtUtil.getUsernameFromToken(token.substring(7));
                } catch (Exception e) {
                    // Token 解析失败，使用默认值
                }
            }
        }

        // 执行方法
        Object result;
        boolean success = true;
        String errorMsg = null;

        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            success = false;
            errorMsg = e.getMessage();
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // 记录日志
            String logMessage = String.format(
                    "[操作日志] 时间: %s | 用户: %s | IP: %s | 操作: %s | 耗时: %dms | 状态: %s%s",
                    LocalDateTime.now().format(FORMATTER),
                    username,
                    ip,
                    operation,
                    duration,
                    success ? "成功" : "失败",
                    errorMsg != null ? " | 错误: " + errorMsg : "");

            if (success) {
                log.info(logMessage);
            } else {
                log.error(logMessage);
            }
        }

        return result;
    }

    /**
     * 获取客户端 IP 地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
