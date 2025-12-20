package com.carsales.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 安全配置类
 * 提供密码加密等安全相关的Bean
 */
@Configuration
public class SecurityConfig {

    /**
     * BCrypt密码编码器
     * 用于密码加密和验证
     * 
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
