package com.carsales.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 用于标记需要记录日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    /**
     * 操作描述
     */
    String value();
}
