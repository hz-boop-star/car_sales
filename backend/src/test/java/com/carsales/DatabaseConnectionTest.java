package com.carsales;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库连接测试
 */
@SpringBootTest
@ActiveProfiles("dev")
public class DatabaseConnectionTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() {
        System.out.println("=== 开始测试数据库连接 ===");

        // 测试基本连接
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertNotNull(result);
        assertEquals(1, result);
        System.out.println("✓ 数据库连接成功！");

        // 测试查询用户表
        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sys_user", Integer.class);
        System.out.println("✓ 用户表查询成功，共有 " + userCount + " 个用户");

        // 测试查询车辆表
        Integer carCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM car_info", Integer.class);
        System.out.println("✓ 车辆表查询成功，共有 " + carCount + " 辆车");

        // 测试查询客户表
        Integer customerCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM customer", Integer.class);
        System.out.println("✓ 客户表查询成功，共有 " + customerCount + " 个客户");

        // 测试查询订单表
        Integer orderCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sales_order", Integer.class);
        System.out.println("✓ 订单表查询成功，共有 " + orderCount + " 个订单");

        System.out.println("=== 数据库连接测试全部通过！===");
    }
}
