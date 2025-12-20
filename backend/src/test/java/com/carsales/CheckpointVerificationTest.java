package com.carsales;

import com.carsales.dto.*;
import com.carsales.entity.CarInfo;
import com.carsales.entity.Customer;
import com.carsales.entity.SalesOrder;
import com.carsales.entity.SysUser;
import com.carsales.exception.BusinessException;
import com.carsales.mapper.CarMapper;
import com.carsales.mapper.CustomerMapper;
import com.carsales.mapper.OrderMapper;
import com.carsales.mapper.UserMapper;
import com.carsales.service.CarService;
import com.carsales.service.CustomerService;
import com.carsales.service.OrderService;
import com.carsales.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 检查点验证测试
 * 任务 7: 检查点 - 核心功能验证
 * 
 * 验证内容：
 * 1. 所有核心 CRUD 功能正常工作
 * 2. 存储过程正确调用
 * 3. 所有约束正确执行
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CheckpointVerificationTest {

    @Autowired
    private CarService carService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    private static Long testCarId;
    private static Long testCustomerId;
    private static Long testUserId;

    @BeforeAll
    static void setupClass() {
        log.info("=".repeat(80));
        log.info("开始执行检查点验证测试");
        log.info("=".repeat(80));
    }

    @AfterAll
    static void teardownClass() {
        log.info("=".repeat(80));
        log.info("检查点验证测试完成");
        log.info("=".repeat(80));
    }

    // ========================================================================
    // 1. 车辆管理 CRUD 测试
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("1.1 车辆创建 - 验证雪花算法ID生成和初始状态")
    void testCarCreate() {
        log.info("\n>>> 测试: 车辆创建");

        CarCreateRequest request = new CarCreateRequest();
        request.setVin("TEST" + System.currentTimeMillis());
        request.setBrand("测试品牌");
        request.setModel("测试型号");
        request.setColor("测试颜色");
        request.setYear(2024);
        request.setPrice(new BigDecimal("300000.00"));

        CarInfo car = carService.createCar(request);

        assertNotNull(car.getId(), "车辆ID应该被自动生成");
        assertTrue(car.getId() > 0, "车辆ID应该大于0");
        assertEquals(0, car.getStatus(), "新车辆初始状态应该为0(在库)");
        assertEquals(request.getVin(), car.getVin());
        assertEquals(request.getBrand(), car.getBrand());

        testCarId = car.getId();
        log.info("✓ 车辆创建成功 - ID: {}, VIN: {}, 状态: {}", car.getId(), car.getVin(), car.getStatus());
    }

    @Test
    @Order(2)
    @DisplayName("1.2 车辆查询 - 验证数据持久化")
    void testCarRead() {
        log.info("\n>>> 测试: 车辆查询");

        assertNotNull(testCarId, "测试车辆ID不应为空");

        CarInfo car = carService.getCarById(testCarId);

        assertNotNull(car);
        assertEquals(testCarId, car.getId());
        assertEquals("测试品牌", car.getBrand());

        log.info("✓ 车辆查询成功 - ID: {}, 品牌: {}, 型号: {}", car.getId(), car.getBrand(), car.getModel());
    }

    @Test
    @Order(3)
    @DisplayName("1.3 车辆更新 - 验证更新功能")
    void testCarUpdate() {
        log.info("\n>>> 测试: 车辆更新");

        assertNotNull(testCarId, "测试车辆ID不应为空");

        CarUpdateRequest request = new CarUpdateRequest();
        request.setId(testCarId);
        request.setColor("更新后的颜色");
        request.setPrice(new BigDecimal("320000.00"));

        CarInfo updatedCar = carService.updateCar(request);

        assertEquals("更新后的颜色", updatedCar.getColor());
        assertEquals(0, new BigDecimal("320000.00").compareTo(updatedCar.getPrice()));

        log.info("✓ 车辆更新成功 - ID: {}, 新颜色: {}, 新价格: {}",
                updatedCar.getId(), updatedCar.getColor(), updatedCar.getPrice());
    }

    @Test
    @Order(4)
    @DisplayName("1.4 车辆唯一性约束 - 验证VIN唯一性")
    void testCarVinUniqueness() {
        log.info("\n>>> 测试: 车辆VIN唯一性约束");

        CarInfo existingCar = carService.getCarById(testCarId);

        CarCreateRequest duplicateRequest = new CarCreateRequest();
        duplicateRequest.setVin(existingCar.getVin()); // 使用已存在的VIN
        duplicateRequest.setBrand("另一个品牌");
        duplicateRequest.setModel("另一个型号");
        duplicateRequest.setPrice(new BigDecimal("200000.00"));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            carService.createCar(duplicateRequest);
        });

        assertEquals(3004, exception.getCode());
        assertTrue(exception.getMessage().contains("车架号已存在"));

        log.info("✓ VIN唯一性约束验证成功 - 重复VIN被正确拒绝");
    }

    @Test
    @Order(5)
    @DisplayName("1.5 车辆查询过滤 - 验证品牌和价格区间过滤")
    void testCarQueryFilters() {
        log.info("\n>>> 测试: 车辆查询过滤");

        CarQueryRequest request = new CarQueryRequest();
        request.setBrand("测试品牌");
        request.setMinPrice(new BigDecimal("300000"));
        request.setMaxPrice(new BigDecimal("400000"));
        request.setPageNum(1);
        request.setPageSize(10);

        var result = carService.queryCarList(request);

        assertTrue(result.getRecords().size() > 0, "应该查询到至少一条记录");
        result.getRecords().forEach(car -> {
            assertEquals("测试品牌", car.getBrand());
            assertTrue(car.getPrice().compareTo(new BigDecimal("300000")) >= 0);
            assertTrue(car.getPrice().compareTo(new BigDecimal("400000")) <= 0);
        });

        log.info("✓ 车辆查询过滤验证成功 - 查询到 {} 条记录", result.getRecords().size());
    }

    // ========================================================================
    // 2. 客户管理 CRUD 测试
    // ========================================================================

    @Test
    @Order(10)
    @DisplayName("2.1 客户创建 - 验证雪花算法ID生成")
    void testCustomerCreate() {
        log.info("\n>>> 测试: 客户创建");

        CustomerCreateRequest request = new CustomerCreateRequest();
        request.setName("测试客户");
        request.setPhone("1380013" + System.currentTimeMillis() % 10000);
        request.setIdCard("11010119900101" + (System.currentTimeMillis() % 10000));
        request.setGender("M");
        request.setAddress("测试地址");

        Customer customer = customerService.createCustomer(request);

        assertNotNull(customer.getId(), "客户ID应该被自动生成");
        assertTrue(customer.getId() > 0, "客户ID应该大于0");
        assertEquals(request.getName(), customer.getName());
        assertEquals(request.getPhone(), customer.getPhone());

        testCustomerId = customer.getId();
        log.info("✓ 客户创建成功 - ID: {}, 姓名: {}, 手机: {}",
                customer.getId(), customer.getName(), customer.getPhone());
    }

    @Test
    @Order(11)
    @DisplayName("2.2 客户查询 - 验证数据持久化")
    void testCustomerRead() {
        log.info("\n>>> 测试: 客户查询");

        assertNotNull(testCustomerId, "测试客户ID不应为空");

        Customer customer = customerService.getCustomerById(testCustomerId);

        assertNotNull(customer);
        assertEquals(testCustomerId, customer.getId());
        assertEquals("测试客户", customer.getName());

        log.info("✓ 客户查询成功 - ID: {}, 姓名: {}", customer.getId(), customer.getName());
    }

    @Test
    @Order(12)
    @DisplayName("2.3 客户更新 - 验证更新功能")
    void testCustomerUpdate() {
        log.info("\n>>> 测试: 客户更新");

        assertNotNull(testCustomerId, "测试客户ID不应为空");

        CustomerUpdateRequest request = new CustomerUpdateRequest();
        request.setId(testCustomerId);
        request.setAddress("更新后的地址");

        Customer updatedCustomer = customerService.updateCustomer(request);

        assertEquals("更新后的地址", updatedCustomer.getAddress());

        log.info("✓ 客户更新成功 - ID: {}, 新地址: {}", updatedCustomer.getId(), updatedCustomer.getAddress());
    }

    @Test
    @Order(13)
    @DisplayName("2.4 客户唯一性约束 - 验证手机号和身份证唯一性")
    void testCustomerUniqueness() {
        log.info("\n>>> 测试: 客户唯一性约束");

        Customer existingCustomer = customerService.getCustomerById(testCustomerId);

        // 测试手机号唯一性
        CustomerCreateRequest duplicatePhoneRequest = new CustomerCreateRequest();
        duplicatePhoneRequest.setName("另一个客户");
        duplicatePhoneRequest.setPhone(existingCustomer.getPhone()); // 重复手机号
        duplicatePhoneRequest.setIdCard("110101199001019999");

        BusinessException phoneException = assertThrows(BusinessException.class, () -> {
            customerService.createCustomer(duplicatePhoneRequest);
        });

        assertEquals(3003, phoneException.getCode());
        assertTrue(phoneException.getMessage().contains("手机号已存在"));

        log.info("✓ 手机号唯一性约束验证成功");

        // 测试身份证号唯一性
        CustomerCreateRequest duplicateIdCardRequest = new CustomerCreateRequest();
        duplicateIdCardRequest.setName("又一个客户");
        duplicateIdCardRequest.setPhone("13800139999");
        duplicateIdCardRequest.setIdCard(existingCustomer.getIdCard()); // 重复身份证号

        BusinessException idCardException = assertThrows(BusinessException.class, () -> {
            customerService.createCustomer(duplicateIdCardRequest);
        });

        assertEquals(3003, idCardException.getCode());
        assertTrue(idCardException.getMessage().contains("身份证号已存在"));

        log.info("✓ 身份证号唯一性约束验证成功");
    }

    @Test
    @Order(14)
    @DisplayName("2.5 客户查询过滤 - 验证姓名模糊搜索")
    void testCustomerQueryFilters() {
        log.info("\n>>> 测试: 客户查询过滤");

        CustomerQueryRequest request = new CustomerQueryRequest();
        request.setName("测试"); // 模糊搜索
        request.setPageNum(1);
        request.setPageSize(10);

        var result = customerService.queryCustomerList(request);

        assertTrue(result.getRecords().size() > 0, "应该查询到至少一条记录");
        result.getRecords().forEach(customer -> {
            assertTrue(customer.getName().contains("测试"));
        });

        log.info("✓ 客户查询过滤验证成功 - 查询到 {} 条记录", result.getRecords().size());
    }

    // ========================================================================
    // 3. 订单管理和存储过程测试
    // ========================================================================

    @Test
    @Order(20)
    @DisplayName("3.1 存储过程 - 验证订单创建和车辆状态更新")
    void testOrderCreationWithStoredProcedure() {
        log.info("\n>>> 测试: 存储过程创建订单");

        // 确保有可用的测试数据
        assertNotNull(testCarId, "测试车辆ID不应为空");
        assertNotNull(testCustomerId, "测试客户ID不应为空");

        // 获取一个销售员用户
        SysUser salesUser = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getRole, "SALESPERSON")
                        .last("LIMIT 1"));

        assertNotNull(salesUser, "应该存在销售员用户");
        testUserId = salesUser.getId();

        // 验证车辆初始状态为"在库"
        CarInfo carBeforeOrder = carService.getCarById(testCarId);
        assertEquals(0, carBeforeOrder.getStatus(), "订单创建前车辆状态应为0(在库)");

        // 调用存储过程创建订单
        var result = orderMapper.callCreateOrderProcedure(
                testUserId,
                testCustomerId,
                testCarId,
                new BigDecimal("310000.00"));

        Integer resultCode = (Integer) result.get("p_result_code");
        String resultMsg = (String) result.get("p_result_msg");
        Object orderIdObj = result.get("p_order_id");

        log.info("存储过程返回 - code: {}, msg: {}, orderId: {}", resultCode, resultMsg, orderIdObj);

        assertEquals(0, resultCode, "存储过程应该成功执行");
        assertNotNull(orderIdObj, "应该返回订单ID");

        // 验证车辆状态已更新为"已售"
        CarInfo carAfterOrder = carService.getCarById(testCarId);
        assertEquals(2, carAfterOrder.getStatus(), "订单创建后车辆状态应为2(已售)");

        log.info("✓ 存储过程验证成功 - 订单创建并车辆状态已更新");
    }

    @Test
    @Order(21)
    @DisplayName("3.2 存储过程约束 - 验证非可售车辆订单防护")
    void testOrderCreationWithSoldCar() {
        log.info("\n>>> 测试: 非可售车辆订单防护");

        // 尝试为已售车辆创建订单
        var result = orderMapper.callCreateOrderProcedure(
                testUserId,
                testCustomerId,
                testCarId, // 这辆车已经被卖了
                new BigDecimal("310000.00"));

        Integer resultCode = (Integer) result.get("p_result_code");
        String resultMsg = (String) result.get("p_result_msg");

        assertNotEquals(0, resultCode, "应该返回错误码");
        assertTrue(resultMsg.contains("不可售") || resultMsg.contains("已售"),
                "错误消息应该指出车辆不可售");

        log.info("✓ 非可售车辆订单防护验证成功 - 错误码: {}, 消息: {}", resultCode, resultMsg);
    }

    @Test
    @Order(22)
    @DisplayName("3.3 约束验证 - 已售车辆删除防护")
    void testSoldCarDeletionPrevention() {
        log.info("\n>>> 测试: 已售车辆删除防护");

        // 尝试删除已售车辆
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            carService.deleteCar(testCarId);
        });

        assertEquals(3006, exception.getCode());
        assertTrue(exception.getMessage().contains("已售车辆不能删除"));

        log.info("✓ 已售车辆删除防护验证成功");
    }

    // ========================================================================
    // 4. 数据库约束测试
    // ========================================================================

    @Test
    @Order(30)
    @DisplayName("4.1 外键约束 - 验证订单关联数据完整性")
    void testForeignKeyConstraints() {
        log.info("\n>>> 测试: 外键约束");

        // 尝试创建订单时使用不存在的客户ID
        var result = orderMapper.callCreateOrderProcedure(
                testUserId,
                999999999L, // 不存在的客户ID
                testCarId,
                new BigDecimal("300000.00"));

        Integer resultCode = (Integer) result.get("p_result_code");

        assertNotEquals(0, resultCode, "应该返回错误码");

        log.info("✓ 外键约束验证成功 - 不存在的关联数据被正确拒绝");
    }

    @Test
    @Order(31)
    @DisplayName("4.2 检查约束 - 验证价格必须大于0")
    void testCheckConstraints() {
        log.info("\n>>> 测试: 检查约束");

        CarCreateRequest request = new CarCreateRequest();
        request.setVin("NEGATIVE" + System.currentTimeMillis());
        request.setBrand("测试品牌");
        request.setModel("测试型号");
        request.setPrice(new BigDecimal("-100.00")); // 负数价格

        // 这应该在数据库层面被拒绝
        assertThrows(Exception.class, () -> {
            carService.createCar(request);
        });

        log.info("✓ 检查约束验证成功 - 无效数据被正确拒绝");
    }

    // ========================================================================
    // 5. 清理测试数据
    // ========================================================================

    @Test
    @Order(100)
    @DisplayName("5.1 清理 - 删除测试客户")
    void cleanupTestCustomer() {
        log.info("\n>>> 清理: 删除测试客户");

        if (testCustomerId != null) {
            customerService.deleteCustomer(testCustomerId);
            log.info("✓ 测试客户已删除 - ID: {}", testCustomerId);
        }
    }

    @Test
    @Order(101)
    @DisplayName("5.2 清理 - 注意：已售车辆无法删除（符合业务规则）")
    void cleanupNote() {
        log.info("\n>>> 清理说明: 测试车辆ID {} 已售出，根据业务规则无法删除", testCarId);
        log.info("这是正确的行为，符合需求 2.9");
    }
}
