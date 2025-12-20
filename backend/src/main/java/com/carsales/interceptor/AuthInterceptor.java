package com.carsales.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carsales.annotation.RequireRole;
import com.carsales.entity.SysUser;
import com.carsales.exception.BusinessException;
import com.carsales.mapper.UserMapper;
import com.carsales.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 权限拦截器
 * 验证用户角色权限
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 只处理方法请求
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);

        // 如果方法没有 @RequireRole 注解，直接放行
        if (requireRole == null) {
            return true;
        }

        // 获取 Token
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            log.warn("权限验证失败：Token 缺失");
            throw new BusinessException(1002, "未登录或 Token 无效");
        }

        token = token.substring(7);

        // 验证 Token 是否过期
        if (jwtUtil.isTokenExpired(token)) {
            log.warn("权限验证失败：Token 已过期");
            throw new BusinessException(1002, "Token 已过期");
        }

        // 获取用户信息
        String username = jwtUtil.getUsernameFromToken(token);

        // 验证 Token 是否有效
        if (!jwtUtil.validateToken(token, username)) {
            log.warn("权限验证失败：Token 无效");
            throw new BusinessException(1002, "Token 无效");
        }

        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));

        if (user == null) {
            log.warn("权限验证失败：用户不存在 - {}", username);
            throw new BusinessException(1002, "用户不存在");
        }

        // 验证角色
        String[] allowedRoles = requireRole.value();
        boolean hasPermission = Arrays.asList(allowedRoles).contains(user.getRole());

        if (!hasPermission) {
            log.warn("权限验证失败：用户 {} 角色 {} 无权访问，需要角色: {}",
                    username, user.getRole(), Arrays.toString(allowedRoles));
            throw new BusinessException(1003, "权限不足");
        }

        log.debug("权限验证通过 - 用户: {}, 角色: {}", username, user.getRole());
        return true;
    }
}
