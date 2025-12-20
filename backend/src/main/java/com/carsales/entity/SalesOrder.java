package com.carsales.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售订单实体类
 * 对应数据库表：sales_order
 */
@Data
@TableName("sales_order")
public class SalesOrder {

    /**
     * 主键ID（使用雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 订单编号（唯一）
     */
    private String orderNo;

    /**
     * 销售员ID（外键）
     */
    private Long salesUserId;

    /**
     * 客户ID（外键）
     */
    private Long customerId;

    /**
     * 车辆ID（外键）
     */
    private Long carId;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 实际成交价
     */
    private BigDecimal actualPrice;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 订单日期
     */
    private LocalDate orderDate;

    /**
     * 订单状态：1-已完成, 2-已取消
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
