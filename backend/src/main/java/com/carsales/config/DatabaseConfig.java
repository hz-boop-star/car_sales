package com.carsales.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.File;

/**
 * 数据库配置类
 * 在 Spring Boot 启动早期创建 SQLite 数据库目录
 */
@Slf4j
public class DatabaseConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String profile = environment.getProperty("spring.profiles.active", "dev");

        // 仅在 dev 环境（SQLite）下创建目录
        if ("dev".equals(profile)) {
            String datasourceUrl = environment.getProperty("spring.datasource.url");

            if (datasourceUrl != null && datasourceUrl.startsWith("jdbc:sqlite:")) {
                // 提取数据库文件路径
                String dbPath = datasourceUrl.replace("jdbc:sqlite:", "");
                File dbFile = new File(dbPath);
                File parentDir = dbFile.getParentFile();

                // 创建父目录
                if (parentDir != null && !parentDir.exists()) {
                    boolean created = parentDir.mkdirs();
                    if (created) {
                        System.out.println("✓ 创建 SQLite 数据库目录: " + parentDir.getAbsolutePath());
                    }
                }

                System.out.println("✓ SQLite 数据库路径: " + dbFile.getAbsolutePath());
            }
        }
    }
}
