package com.carsales.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.carsales.dto.OrderCreateRequest;
import com.carsales.dto.OrderDetailVO;
import com.carsales.dto.OrderQueryRequest;
import com.carsales.entity.SalesOrder;
import com.carsales.entity.SysUser;
import com.carsales.exception.BusinessException;
import com.carsales.mapper.OrderMapper;
import com.carsales.mapper.UserMapper;
import com.carsales.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 订单服务类
 * 处理订单管理相关业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    private final JwtUtil jwtUtil;

    /**
     * 创建订单
     * 
     * @param request 订单创建请求
     * @param token   JWT Token（用于获取当前登录用户）
     * @return 创建的订单详情
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderDetailVO createOrder(OrderCreateRequest request, String token) {
        // 从token中获取当前登录用户ID
        String username = jwtUtil.getUsernameFromToken(token);
        SysUser currentUser = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));

        if (currentUser == null) {
            log.warn("订单创建失败：用户不存在 - username: {}", username);
            throw new BusinessException(1002, "用户不存在或Token无效");
        }

        Long salesUserId = currentUser.getId();

        log.info("开始创建订单 - 销售员: {}, 客户ID: {}, 车辆ID: {}, 成交价: {}",
                currentUser.getRealName(), request.getCustomerId(), request.getCarId(), request.getActualPrice());

        // ========== SQLite 实现（当前使用） ==========
        // 生成订单号（格式：ORD + 时间戳）
        String orderNo = "ORD" + System.currentTimeMillis();

        // 创建订单对象
        SalesOrder order = new SalesOrder();
        order.setOrderNo(orderNo);
        order.setSalesUserId(salesUserId);
        order.setCustomerId(request.getCustomerId());
        order.setCarId(request.getCarId());
        order.setOriginalPrice(request.getOriginalPrice());
        order.setDiscountAmount(request.getDiscountAmount());
        order.setActualPrice(request.getActualPrice());
        order.setOrderDate(LocalDate.parse(request.getOrderDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        order.setStatus(1); // 默认状态：已完成
        order.setRemark(request.getRemark());

        // 插入订单
        int result = orderMapper.insert(order);
        if (result <= 0) {
            log.error("订单创建失败：数据库插入失败");
            throw new BusinessException(5004, "订单创建失败");
        }

        log.info("订单创建成功 - 订单ID: {}, 订单号: {}", order.getId(), orderNo);

        // 查询并返回订单详情
        return getOrderDetailById(order.getId());

        // ========== PostgreSQL 存储过程实现（暂时注释，将来切换回 PostgreSQL 时使用） ==========
        /*
        // 调用存储过程创建订单
        Map<String, Object> procResult = orderMapper.callCreateOrderProc(
                salesUserId,
                request.getCustomerId(),
                request.getCarId(),
                request.getOriginalPrice(),
                request.getDiscountAmount(),
                request.getActualPrice(),
                request.getOrderDate(),
                request.getRemark()
        );

        // 解析存储过程返回结果
        Integer resultCode = getIntegerFromMap(procResult, "p_result_code");
        String resultMsg = (String) procResult.get("p_result_msg");
        Long orderId = getLongFromMap(procResult, "p_order_id");

        log.info("存储过程执行完成 - 结果码: {}, 消息: {}, 订单ID: {}", resultCode, resultMsg, orderId);

        // 检查执行结果
        if (resultCode == null || resultCode != 0) {
            log.error("订单创建失败 - 结果码: {}, 消息: {}", resultCode, resultMsg);
            throw new BusinessException(5004, resultMsg != null ? resultMsg : "订单创建失败");
        }

        if (orderId == null) {
            log.error("订单创建失败：未返回订单ID");
            throw new BusinessException(5004, "订单创建失败");
        }

        log.info("订单创建成功 - 订单ID: {}", orderId);

        // 查询并返回订单详情
        return getOrderDetailById(orderId);
        */
    }

    /**
     * 根据ID查询订单详情
     * 
     * @param orderId 订单ID
     * @return 订单详情
     */
    public OrderDetailVO getOrderDetailById(Long orderId) {
        OrderDetailVO orderDetail = orderMapper.selectOrderDetailById(orderId);
        if (orderDetail == null) {
            log.warn("订单查询失败：订单不存在 - ID: {}", orderId);
            throw new BusinessException(3001, "订单不存在");
        }
        return orderDetail;
    }

    /**
     * 分页查询订单列表（支持多条件筛选）
     * 返回包含关联信息的订单详情
     * 
     * @param request 查询请求
     * @return 分页结果
     */
    public Page<OrderDetailVO> queryOrderList(OrderQueryRequest request) {
        // 解析日期参数
        LocalDate startDate = null;
        LocalDate endDate = null;
        if (request.getStartDate() != null && !request.getStartDate().isEmpty()) {
            startDate = LocalDate.parse(request.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        }
        if (request.getEndDate() != null && !request.getEndDate().isEmpty()) {
            endDate = LocalDate.parse(request.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        }

        // 创建分页对象
        Page<OrderDetailVO> page = new Page<>(request.getPageNum(), request.getPageSize());

        // 调用 Mapper 查询订单详情列表
        Page<OrderDetailVO> result = orderMapper.selectOrderDetailPage(
                page,
                startDate,
                endDate,
                request.getSalesUserId(),
                request.getStatus());

        log.debug("订单查询 - 日期区间: [{}, {}], 销售员ID: {}, 状态: {}, 结果数: {}",
                request.getStartDate(), request.getEndDate(), request.getSalesUserId(),
                request.getStatus(), result.getRecords().size());

        return result;
    }

    /**
     * 从Map中安全获取Integer值（用于PostgreSQL存储过程）
     */
    @SuppressWarnings("unused")
    private Integer getIntegerFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return null;
    }

    /**
     * 从Map中安全获取Long值（用于PostgreSQL存储过程）
     */
    @SuppressWarnings("unused")
    private Long getLongFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).longValue();
        }
        return null;
    }
}
