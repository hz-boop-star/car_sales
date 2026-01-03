package com.carsales.exception;

import com.carsales.common.Result;
import com.carsales.common.ResultCode;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * Global Exception Handler
 * 
 * Validates: Requirements 10.3
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * Handle business exceptions
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理 JWT 过期异常
     * Handle JWT expired exceptions
     */
    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleExpiredJwtException(ExpiredJwtException e) {
        log.warn("Token 已过期: {}", e.getMessage());
        return Result.error(ResultCode.TOKEN_INVALID, "登录已过期，请重新登录");
    }

    /**
     * 处理参数验证异常（@Valid）
     * Handle validation exceptions
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数验证失败: {}", errorMessage);
        return Result.error(ResultCode.PARAM_FORMAT_ERROR, errorMessage);
    }

    /**
     * 处理参数绑定异常
     * Handle bind exceptions
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        String errorMessage = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定失败: {}", errorMessage);
        return Result.error(ResultCode.PARAM_FORMAT_ERROR, errorMessage);
    }

    /**
     * 处理约束违反异常
     * Handle constraint violation exceptions
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("约束违反: {}", errorMessage);
        return Result.error(ResultCode.DB_UNIQUE_CONSTRAINT_VIOLATION, "数据约束冲突: " + errorMessage);
    }

    /**
     * 处理数据完整性违反异常
     * Handle data integrity violation exceptions
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据完整性错误", e);
        String message = e.getMessage();

        // 判断具体的约束类型
        if (message != null) {
            if (message.contains("unique") || message.contains("duplicate")) {
                return Result.error(ResultCode.DB_UNIQUE_CONSTRAINT_VIOLATION, "数据已存在，违反唯一性约束");
            } else if (message.contains("foreign key")) {
                return Result.error(ResultCode.DB_FOREIGN_KEY_VIOLATION, "外键约束冲突，关联数据不存在或被引用");
            } else if (message.contains("check")) {
                return Result.error(ResultCode.DB_CHECK_CONSTRAINT_VIOLATION, "数据不符合检查约束");
            }
        }

        return Result.error(ResultCode.DB_UNIQUE_CONSTRAINT_VIOLATION, "数据完整性错误");
    }

    /**
     * 处理所有未捕获的异常
     * Handle all uncaught exceptions
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        // 忽略静态资源 404 错误（爬虫访问）
        if (e instanceof org.springframework.web.servlet.resource.NoResourceFoundException) {
            log.debug("静态资源未找到: {}", e.getMessage());
            return Result.error(ResultCode.SYSTEM_UNKNOWN_ERROR, "资源未找到");
        }

        log.error("系统异常", e);
        return Result.error(ResultCode.SYSTEM_UNKNOWN_ERROR, "系统错误，请联系管理员");
    }

}
