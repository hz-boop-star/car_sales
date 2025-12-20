package com.carsales.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carsales.dto.DashboardVO;
import com.carsales.dto.MonthlySalesTrendVO;
import com.carsales.dto.SalesStatisticsVO;
import com.carsales.entity.CarInfo;
import com.carsales.entity.SalesOrder;
import com.carsales.mapper.CarMapper;
import com.carsales.mapper.OrderMapper;
import com.carsales.mapper.StatisticsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsMapper statisticsMapper;
    private final CarMapper carMapper;
    private final OrderMapper orderMapper;

    /**
     * 查询销售统计
     */
    public List<SalesStatisticsVO> getSalesStatistics(String brand) {
        if (brand != null && !brand.isEmpty()) {
            return statisticsMapper.selectSalesStatisticsByBrand(brand);
        }
        return statisticsMapper.selectSalesStatistics();
    }

    /**
     * 查询月度销售趋势
     */
    public List<MonthlySalesTrendVO> getMonthlySalesTrend(String startMonth, String endMonth) {
        if (startMonth != null && endMonth != null) {
            return statisticsMapper.selectMonthlySalesTrendByRange(startMonth, endMonth);
        }
        return statisticsMapper.selectMonthlySalesTrend();
    }

    /**
     * 获取仪表盘数据
     */
    public DashboardVO getDashboardData() {
        DashboardVO dashboard = new DashboardVO();

        // 统计车辆数据
        Long totalCars = carMapper.selectCount(null);
        Long availableCars = carMapper.selectCount(
                new LambdaQueryWrapper<CarInfo>().eq(CarInfo::getStatus, 0));
        Long soldCars = carMapper.selectCount(
                new LambdaQueryWrapper<CarInfo>().eq(CarInfo::getStatus, 2));

        dashboard.setTotalCars(totalCars);
        dashboard.setAvailableCars(availableCars);
        dashboard.setSoldCars(soldCars);

        // 统计订单数据
        Long totalOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<SalesOrder>().eq(SalesOrder::getStatus, 1));

        dashboard.setTotalOrders(totalOrders);

        // 计算总收入
        List<SalesOrder> orders = orderMapper.selectList(
                new LambdaQueryWrapper<SalesOrder>().eq(SalesOrder::getStatus, 1));
        BigDecimal totalRevenue = orders.stream()
                .map(SalesOrder::getActualPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        dashboard.setTotalRevenue(totalRevenue);

        // 品牌销量分布
        List<SalesStatisticsVO> salesStats = statisticsMapper.selectSalesStatistics();
        Map<String, Long> brandCountMap = salesStats.stream()
                .collect(Collectors.groupingBy(
                        SalesStatisticsVO::getBrand,
                        Collectors.summingLong(SalesStatisticsVO::getSalesCount)));

        List<DashboardVO.BrandDistribution> brandDistribution = new ArrayList<>();
        brandCountMap.forEach((brand, count) -> {
            DashboardVO.BrandDistribution dist = new DashboardVO.BrandDistribution();
            dist.setBrand(brand);
            dist.setCount(count);
            brandDistribution.add(dist);
        });
        dashboard.setBrandDistribution(brandDistribution);

        // 月度销售趋势（最近6个月）
        List<MonthlySalesTrendVO> trendList = statisticsMapper.selectMonthlySalesTrend();
        List<DashboardVO.MonthlyTrend> monthlyTrend = trendList.stream()
                .limit(6)
                .map(trend -> {
                    DashboardVO.MonthlyTrend mt = new DashboardVO.MonthlyTrend();
                    mt.setMonth(trend.getMonth());
                    mt.setSales(trend.getSalesCount());
                    return mt;
                })
                .collect(Collectors.toList());
        dashboard.setMonthlyTrend(monthlyTrend);

        log.info("仪表盘数据查询完成 - 总车辆: {}, 可售: {}, 已售: {}, 订单: {}, 收入: {}",
                totalCars, availableCars, soldCars, totalOrders, totalRevenue);

        return dashboard;
    }
}
