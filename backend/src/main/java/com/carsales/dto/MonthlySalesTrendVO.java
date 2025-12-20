package com.carsales.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 月度销售趋势 VO
 */
@Data
public class MonthlySalesTrendVO {
    private String month;
    private Long salesCount;
    private BigDecimal totalAmount;
    private BigDecimal avgPrice;
    private Long activeSalespersonCount;
}
