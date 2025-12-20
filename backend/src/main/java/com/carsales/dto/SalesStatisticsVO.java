package com.carsales.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售统计 VO
 */
@Data
public class SalesStatisticsVO {
    private String brand;
    private String model;
    private Long salesCount;
    private BigDecimal totalAmount;
    private BigDecimal avgPrice;
    private LocalDate firstSaleDate;
    private LocalDate lastSaleDate;
    private Long salespersonId;
    private String salespersonName;
}
