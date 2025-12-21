package com.carsales.controller;

import com.carsales.annotation.RequireRole;
import com.carsales.common.Result;
import com.carsales.dto.DashboardVO;
import com.carsales.dto.MonthlySalesTrendVO;
import com.carsales.dto.SalesStatisticsVO;
import com.carsales.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 统计报表控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 查询销售统计
     */
    @GetMapping("/sales")
    @RequireRole({ "ADMIN", "SALES_MANAGER" })
    public Result<List<SalesStatisticsVO>> getSalesStatistics(
            @RequestParam(required = false) String brand) {
        log.info("收到销售统计查询请求 - 品牌: {}", brand);
        List<SalesStatisticsVO> statistics = statisticsService.getSalesStatistics(brand);
        return Result.success(statistics);
    }

    /**
     * 查询月度销售趋势
     */
    @GetMapping("/trend")
    public Result<List<MonthlySalesTrendVO>> getMonthlySalesTrend(
            @RequestParam(required = false) String startMonth,
            @RequestParam(required = false) String endMonth) {
        log.info("收到销售趋势查询请求 - 月份范围: [{}, {}]", startMonth, endMonth);
        List<MonthlySalesTrendVO> trend = statisticsService.getMonthlySalesTrend(startMonth, endMonth);
        return Result.success(trend);
    }

    /**
     * 获取仪表盘数据
     */
    @GetMapping("/dashboard")
    public Result<DashboardVO> getDashboardData() {
        log.info("收到仪表盘数据查询请求");
        DashboardVO dashboard = statisticsService.getDashboardData();
        return Result.success(dashboard);
    }
}
