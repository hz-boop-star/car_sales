package com.carsales.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户信息实体类
 * 对应数据库表：customer
 */
@Data
@TableName("customer")
public class Customer {

    /**
     * 主键ID（使用雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 客户姓名
     */
    private String name;

    /**
     * 手机号（唯一）
     */
    private String phone;

    /**
     * 身份证号（唯一）
     */
    private String idCard;

    /**
     * 性别：M-男, F-女
     */
    private String gender;

    /**
     * 地址
     */
    private String address;

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
