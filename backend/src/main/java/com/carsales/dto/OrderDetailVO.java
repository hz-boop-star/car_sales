package com.carsales.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单详情VO（包含关联的用户、客户、车辆信息）
 */
@Data
public class OrderDetailVO {

    // 订单基本信息
    private Long id;
    private String orderNo;
    private BigDecimal originalPrice;
    private BigDecimal actualPrice;
    private BigDecimal discountAmount;
    private LocalDate orderDate;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 销售员信息
    private Long salesUserId;
    private String salesUserName;
    private String salesUserPhone;

    // 客户信息
    private Long customerId;
    private String customerName;
    private String customerPhone;
    private String customerIdCard;

    // 车辆信息
    private Long carId;
    private String carVin;
    private String carBrand;
    private String carModel;
    private String carColor;
    private Integer carYear;
}
