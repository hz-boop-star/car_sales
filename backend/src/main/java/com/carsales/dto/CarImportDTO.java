package com.carsales.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 车辆导入 DTO
 * 用于 Excel 导入
 */
@Data
public class CarImportDTO {

    @ExcelProperty(value = "车架号", index = 0)
    private String vin;

    @ExcelProperty(value = "品牌", index = 1)
    private String brand;

    @ExcelProperty(value = "型号", index = 2)
    private String model;

    @ExcelProperty(value = "颜色", index = 3)
    private String color;

    @ExcelProperty(value = "年份", index = 4)
    private Integer year;

    @ExcelProperty(value = "价格", index = 5)
    private BigDecimal price;

    @ExcelProperty(value = "状态", index = 6)
    private Integer status;

    @ExcelProperty(value = "采购日期", index = 7)
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate purchaseDate;
}
