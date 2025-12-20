package com.carsales.annotation;

import java.lang.annotation.*;

/**
 * 权限注解
 * 用于标记需要特定角色才能访问的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    /**
     * 允许的角色列表
     */
    String[] value();
}
