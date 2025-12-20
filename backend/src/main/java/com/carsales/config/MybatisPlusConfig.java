package com.carsales.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus配置
 * MyBatis-Plus Configuration
 * 
 * Validates: Requirements 7.2 (Snowflake ID generation)
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件
     * Pagination plugin
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件（PostgreSQL）
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }

    /**
     * 自定义ID生成器（雪花算法）
     * Custom ID generator using Snowflake algorithm
     * 
     * MyBatis-Plus默认使用雪花算法生成ID
     * MyBatis-Plus uses Snowflake algorithm by default
     * 这里可以自定义workerId和datacenterId
     * Here we can customize workerId and datacenterId
     */
    @Bean
    public IdentifierGenerator identifierGenerator() {
        return new IdentifierGenerator() {
            @Override
            public Number nextId(Object entity) {
                // 使用MyBatis-Plus默认的雪花算法实现
                // Use MyBatis-Plus default Snowflake implementation
                // workerId和datacenterId会自动从机器信息中获取
                // workerId and datacenterId are automatically obtained from machine info
                return com.baomidou.mybatisplus.core.toolkit.IdWorker.getId();
            }

            @Override
            public String nextUUID(Object entity) {
                return com.baomidou.mybatisplus.core.toolkit.IdWorker.get32UUID();
            }
        };
    }

}
