package com.carsales.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 车辆信息实体类
 * 对应数据库表：car_info
 */
@Data
@TableName("car_info")
public class CarInfo {

    /**
     * 主键ID（使用雪花算法生成）
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ExcelIgnore
    private Long id;

    /**
     * 车架号VIN（17位唯一标识）
     */
    @ExcelProperty(value = "车架号", index = 0)
    private String vin;

    /**
     * 品牌
     */
    @ExcelProperty(value = "品牌", index = 1)
    private String brand;

    /**
     * 型号
     */
    @ExcelProperty(value = "型号", index = 2)
    private String model;

    /**
     * 颜色
     */
    @ExcelProperty(value = "颜色", index = 3)
    private String color;

    /**
     * 年份
     */
    @ExcelProperty(value = "年份", index = 4)
    private Integer year;

    /**
     * 价格
     */
    @ExcelProperty(value = "价格", index = 5)
    private BigDecimal price;

    /**
     * 状态：0-在库, 1-锁定, 2-已售
     */
    @ExcelProperty(value = "状态(0-在库/1-锁定/2-已售)", index = 6)
    private Integer status;

    /**
     * 进货日期
     */
    @ExcelProperty(value = "进货日期", index = 7)
    private LocalDate purchaseDate;

    /**
     * 创建时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT)
    @ExcelIgnore
    private LocalDateTime createTime;

    /**
     * 更新时间（自动填充）
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private LocalDateTime updateTime;
}
