package com.carsales.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsales.dto.CustomerCreateRequest;
import com.carsales.dto.CustomerQueryRequest;
import com.carsales.dto.CustomerUpdateRequest;
import com.carsales.entity.Customer;
import com.carsales.exception.BusinessException;
import com.carsales.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户服务类
 * 处理客户管理相关业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerMapper customerMapper;
    
    /**
     * 创建客户
     * 
     * @param request 客户创建请求
     * @return 创建的客户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Customer createCustomer(CustomerCreateRequest request) {
        // 检查手机号是否已存在
        LambdaQueryWrapper<Customer> phoneQuery = new LambdaQueryWrapper<>();
        phoneQuery.eq(Customer::getPhone, request.getPhone());
        Long phoneCount = customerMapper.selectCount(phoneQuery);
        
        if (phoneCount > 0) {
            log.warn("客户创建失败：手机号已存在 - {}", request.getPhone());
            throw new BusinessException(3003, "手机号已存在");
        }
        
        // 检查身份证号是否已存在
        LambdaQueryWrapper<Customer> idCardQuery = new LambdaQueryWrapper<>();
        idCardQuery.eq(Customer::getIdCard, request.getIdCard());
        Long idCardCount = customerMapper.selectCount(idCardQuery);
        
        if (idCardCount > 0) {
            log.warn("客户创建失败：身份证号已存在 - {}", request.getIdCard());
            throw new BusinessException(3003, "身份证号已存在");
        }
        
        // 创建客户实体
        Customer customer = new Customer();
        BeanUtils.copyProperties(request, customer);
        
        // 保存到数据库
        customerMapper.insert(customer);
        
        log.info("客户创建成功 - 姓名: {}, 手机号: {}", customer.getName(), customer.getPhone());
        
        return customer;
    }
    
    /**
     * 更新客户信息
     * 
     * @param request 客户更新请求
     * @return 更新后的客户信息
     */
    @Transactional(rollbackFor = Exception.class)
    public Customer updateCustomer(CustomerUpdateRequest request) {
        // 检查客户是否存在
        Customer existingCustomer = customerMapper.selectById(request.getId());
        if (existingCustomer == null) {
            log.warn("客户更新失败：客户不存在 - ID: {}", request.getId());
            throw new BusinessException(3007, "客户不存在");
        }
        
        // 如果更新手机号，检查新手机号是否已被其他客户使用
        if (request.getPhone() != null && !request.getPhone().equals(existingCustomer.getPhone())) {
            LambdaQueryWrapper<Customer> phoneQuery = new LambdaQueryWrapper<>();
            phoneQuery.eq(Customer::getPhone, request.getPhone())
                     .ne(Customer::getId, request.getId());
            Long phoneCount = customerMapper.selectCount(phoneQuery);
            
            if (phoneCount > 0) {
                log.warn("客户更新失败：手机号已被其他客户使用 - {}", request.getPhone());
                throw new BusinessException(3003, "手机号已存在");
            }
        }
        
        // 如果更新身份证号，检查新身份证号是否已被其他客户使用
        if (request.getIdCard() != null && !request.getIdCard().equals(existingCustomer.getIdCard())) {
            LambdaQueryWrapper<Customer> idCardQuery = new LambdaQueryWrapper<>();
            idCardQuery.eq(Customer::getIdCard, request.getIdCard())
                      .ne(Customer::getId, request.getId());
            Long idCardCount = customerMapper.selectCount(idCardQuery);
            
            if (idCardCount > 0) {
                log.warn("客户更新失败：身份证号已被其他客户使用 - {}", request.getIdCard());
                throw new BusinessException(3003, "身份证号已存在");
            }
        }
        
        // 更新客户信息（只更新非null字段）
        Customer customer = new Customer();
        BeanUtils.copyProperties(request, customer);
        customerMapper.updateById(customer);
        
        log.info("客户更新成功 - ID: {}, 姓名: {}", request.getId(), request.getName());
        
        // 返回更新后的完整信息
        return customerMapper.selectById(request.getId());
    }
    
    /**
     * 删除客户
     * 
     * @param id 客户ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCustomer(Long id) {
        // 检查客户是否存在
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            log.warn("客户删除失败：客户不存在 - ID: {}", id);
            throw new BusinessException(3007, "客户不存在");
        }
        
        // TODO: 检查客户是否有关联订单
        // 当订单模块实现后，需要添加以下检查：
        // LambdaQueryWrapper<SalesOrder> orderQuery = new LambdaQueryWrapper<>();
        // orderQuery.eq(SalesOrder::getCustomerId, id);
        // Long orderCount = orderMapper.selectCount(orderQuery);
        // if (orderCount > 0) {
        //     log.warn("客户删除失败：客户有关联订单 - ID: {}, 订单数: {}", id, orderCount);
        //     throw new BusinessException(3005, "客户有关联订单，无法删除");
        // }
        
        // 删除客户
        customerMapper.deleteById(id);
        
        log.info("客户删除成功 - ID: {}, 姓名: {}", id, customer.getName());
    }
    
    /**
     * 根据ID查询客户
     * 
     * @param id 客户ID
     * @return 客户信息
     */
    public Customer getCustomerById(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new BusinessException(3007, "客户不存在");
        }
        return customer;
    }
    
    /**
     * 分页查询客户列表（支持姓名模糊搜索和手机号精确搜索）
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    public Page<Customer> queryCustomerList(CustomerQueryRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<Customer> queryWrapper = new LambdaQueryWrapper<>();
        
        // 姓名模糊匹配
        if (request.getName() != null && !request.getName().isEmpty()) {
            queryWrapper.like(Customer::getName, request.getName());
        }
        
        // 手机号精确匹配
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            queryWrapper.eq(Customer::getPhone, request.getPhone());
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Customer::getCreateTime);
        
        // 分页查询
        Page<Customer> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<Customer> result = customerMapper.selectPage(page, queryWrapper);
        
        log.debug("客户查询 - 姓名: {}, 手机号: {}, 结果数: {}", 
                 request.getName(), request.getPhone(), result.getRecords().size());
        
        return result;
    }
}
