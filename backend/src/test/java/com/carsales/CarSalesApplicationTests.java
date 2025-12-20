package com.carsales;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 应用上下文加载测试
 * Application Context Load Test
 */
@SpringBootTest
@ActiveProfiles("dev")
class CarSalesApplicationTests {

    @Test
    void contextLoads() {
        // 测试Spring应用上下文是否能正常加载
        // Test if Spring application context loads successfully
    }

}
