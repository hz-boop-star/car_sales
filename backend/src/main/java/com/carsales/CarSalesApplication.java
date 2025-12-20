package com.carsales;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 汽车销售管理系统主应用类
 * Car Sales Management System Main Application
 */
@SpringBootApplication
@MapperScan("com.carsales.mapper")
public class CarSalesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarSalesApplication.class, args);
    }

}
