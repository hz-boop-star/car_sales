package com.carsales.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsales.annotation.OperationLog;
import com.carsales.common.Result;
import com.carsales.dto.OrderCreateRequest;
import com.carsales.dto.OrderDetailVO;
import com.carsales.dto.OrderQueryRequest;
import com.carsales.entity.SalesOrder;
import com.carsales.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理控制器
 * 提供订单创建、查询接口
 */
@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单（调用存储过程）
     * 
     * @param request 订单创建请求
     * @param token   JWT Token（从请求头获取）
     * @return 创建的订单详情
     */
    @PostMapping
    @OperationLog("创建订单")
    public Result<OrderDetailVO> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            @RequestHeader("Authorization") String token) {
        log.info("收到订单创建请求 - 客户ID: {}, 车辆ID: {}, 成交价: {}",
                request.getCustomerId(), request.getCarId(), request.getActualPrice());

        // 移除 "Bearer " 前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        OrderDetailVO orderDetail = orderService.createOrder(request, token);
        return Result.success("订单创建成功", orderDetail);
    }

    /**
     * 根据ID查询订单详情
     * 
     * @param id 订单ID
     * @return 订单详情（包含关联的用户、客户、车辆信息）
     */
    @GetMapping("/{id}")
    public Result<OrderDetailVO> getOrderDetailById(@PathVariable Long id) {
        log.info("收到订单详情查询请求 - ID: {}", id);
        OrderDetailVO orderDetail = orderService.getOrderDetailById(id);
        return Result.success(orderDetail);
    }

    /**
     * 分页查询订单列表（支持多条件筛选）
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    @GetMapping
    public Result<Page<SalesOrder>> queryOrderList(OrderQueryRequest request) {
        log.info("收到订单列表查询请求 - 日期区间: [{}, {}], 销售员ID: {}, 状态: {}, 页码: {}",
                request.getStartDate(), request.getEndDate(), request.getSalesUserId(),
                request.getStatus(), request.getPageNum());
        Page<SalesOrder> page = orderService.queryOrderList(request);
        return Result.success(page);
    }
}
