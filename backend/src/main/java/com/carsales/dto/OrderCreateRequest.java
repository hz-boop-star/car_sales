package com.carsales.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单创建请求DTO
 */
@Data
public class OrderCreateRequest {

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 车辆ID
     */
    @NotNull(message = "车辆ID不能为空")
    private Long carId;

    /**
     * 实际成交价
     */
    @NotNull(message = "实际成交价不能为空")
    @Positive(message = "实际成交价必须大于0")
    private BigDecimal actualPrice;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 订单日期
     */
    private String orderDate;

    /**
     * 备注
     */
    private String remark;
}
