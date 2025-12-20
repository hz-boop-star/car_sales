package com.carsales.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsales.annotation.RequireRole;
import com.carsales.common.Result;
import com.carsales.dto.CustomerCreateRequest;
import com.carsales.dto.CustomerQueryRequest;
import com.carsales.dto.CustomerUpdateRequest;
import com.carsales.entity.Customer;
import com.carsales.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 客户管理控制器
 * 提供客户CRUD和查询接口
 */
@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 创建客户
     * 
     * @param request 客户创建请求
     * @return 创建的客户信息
     */
    @PostMapping
    public Result<Customer> createCustomer(@Valid @RequestBody CustomerCreateRequest request) {
        log.info("收到客户创建请求 - 姓名: {}, 手机号: {}",
                request.getName(), request.getPhone());
        Customer customer = customerService.createCustomer(request);
        return Result.success("客户创建成功", customer);
    }

    /**
     * 更新客户信息
     * 
     * @param id      客户ID
     * @param request 客户更新请求
     * @return 更新后的客户信息
     */
    @PutMapping("/{id}")
    public Result<Customer> updateCustomer(@PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest request) {
        log.info("收到客户更新请求 - ID: {}", id);
        request.setId(id);
        Customer customer = customerService.updateCustomer(request);
        return Result.success("客户更新成功", customer);
    }

    /**
     * 删除客户
     * 
     * @param id 客户ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCustomer(@PathVariable Long id) {
        log.info("收到客户删除请求 - ID: {}", id);
        customerService.deleteCustomer(id);
        return Result.success("客户删除成功");
    }

    /**
     * 根据ID查询客户
     * 
     * @param id 客户ID
     * @return 客户信息
     */
    @GetMapping("/{id}")
    public Result<Customer> getCustomerById(@PathVariable Long id) {
        log.info("收到客户查询请求 - ID: {}", id);
        Customer customer = customerService.getCustomerById(id);
        return Result.success(customer);
    }

    /**
     * 分页查询客户列表（支持姓名模糊搜索和手机号精确搜索）
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    @GetMapping
    public Result<Page<Customer>> queryCustomerList(CustomerQueryRequest request) {
        log.info("收到客户列表查询请求 - 姓名: {}, 手机号: {}, 页码: {}",
                request.getName(), request.getPhone(), request.getPageNum());
        Page<Customer> page = customerService.queryCustomerList(request);
        return Result.success(page);
    }
}
