package com.carsales;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 简单的数据库连接测试（不依赖Spring）
 */
public class SimpleDBTest {

    public static void main(String[] args) {
        String url = "jdbc:postgresql://124.70.48.79:26000/car_sales_db";
        String username = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "gaussdb";
        String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "Gauss@123";

        System.out.println("=== 数据库连接测试 ===");
        System.out.println("URL: " + url);
        System.out.println("Username: " + username);
        System.out.println();

        try {
            // 显式加载驱动
            Class.forName("org.postgresql.Driver");

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✓ 数据库连接成功！");
            System.out.println();

            Statement stmt = conn.createStatement();

            // 测试查询用户表
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM sys_user");
            if (rs.next()) {
                System.out.println("✓ 用户表查询成功，共有 " + rs.getInt("cnt") + " 个用户");
            }

            // 测试查询车辆表
            rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM car_info");
            if (rs.next()) {
                System.out.println("✓ 车辆表查询成功，共有 " + rs.getInt("cnt") + " 辆车");
            }

            // 测试查询客户表
            rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM customer");
            if (rs.next()) {
                System.out.println("✓ 客户表查询成功，共有 " + rs.getInt("cnt") + " 个客户");
            }

            // 测试查询订单表
            rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM sales_order");
            if (rs.next()) {
                System.out.println("✓ 订单表查询成功，共有 " + rs.getInt("cnt") + " 个订单");
            }

            // 测试查询视图
            rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM v_sales_statistics");
            if (rs.next()) {
                System.out.println("✓ 销售统计视图查询成功，共有 " + rs.getInt("cnt") + " 条记录");
            }

            System.out.println();
            System.out.println("=== 所有测试通过！后端与数据库连接正常 ===");

        } catch (Exception e) {
            System.err.println("✗ 数据库连接失败：" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
