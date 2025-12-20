package com.carsales.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 仪表盘数据 VO
 */
@Data
public class DashboardVO {
    private Long totalCars;
    private Long availableCars;
    private Long soldCars;
    private Long totalOrders;
    private BigDecimal totalRevenue;
    private List<BrandDistribution> brandDistribution;
    private List<MonthlyTrend> monthlyTrend;

    @Data
    public static class BrandDistribution {
        private String brand;
        private Long count;
    }

    @Data
    public static class MonthlyTrend {
        private String month;
        private Long sales;
    }
}
