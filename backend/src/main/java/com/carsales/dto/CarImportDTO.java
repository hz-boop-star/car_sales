package com.carsales.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 车辆导入 DTO
 * 用于 Excel 导入
 */
@Data
public class CarImportDTO {

    @ExcelProperty("车架号")
    private String vin;

    @ExcelProperty("品牌")
    private String brand;

    @ExcelProperty("型号")
    private String model;

    @ExcelProperty("颜色")
    private String color;

    @ExcelProperty("年份")
    private Integer year;

    @ExcelProperty("价格")
    private BigDecimal price;
}
