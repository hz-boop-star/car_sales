package com.carsales.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 车辆更新请求DTO
 */
@Data
public class CarUpdateRequest {

    /**
     * 车辆ID
     */
    @NotNull(message = "车辆ID不能为空")
    private Long id;

    /**
     * 车架号VIN（17位）
     */
    @Size(min = 17, max = 17, message = "车架号必须为17位")
    private String vin;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 型号
     */
    private String model;

    /**
     * 颜色
     */
    private String color;

    /**
     * 年份
     */
    @Min(value = 1900, message = "年份不能小于1900")
    @Max(value = 2100, message = "年份不能大于2100")
    private Integer year;

    /**
     * 价格
     */
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    /**
     * 进货日期
     */
    private LocalDate purchaseDate;
}
