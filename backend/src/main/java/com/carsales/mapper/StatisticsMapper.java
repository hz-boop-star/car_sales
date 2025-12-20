package com.carsales.mapper;

import com.carsales.dto.MonthlySalesTrendVO;
import com.carsales.dto.SalesStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 统计数据 Mapper
 * 查询数据库视图
 */
@Mapper
public interface StatisticsMapper {

    /**
     * 查询销售统计（从视图）
     */
    @Select("SELECT * FROM v_sales_statistics ORDER BY total_amount DESC")
    List<SalesStatisticsVO> selectSalesStatistics();

    /**
     * 按品牌查询销售统计
     */
    @Select("SELECT * FROM v_sales_statistics WHERE brand = #{brand} ORDER BY total_amount DESC")
    List<SalesStatisticsVO> selectSalesStatisticsByBrand(@Param("brand") String brand);

    /**
     * 查询月度销售趋势（从视图）
     */
    @Select("SELECT * FROM v_monthly_sales_trend ORDER BY month DESC")
    List<MonthlySalesTrendVO> selectMonthlySalesTrend();

    /**
     * 查询指定月份范围的销售趋势
     */
    @Select("SELECT * FROM v_monthly_sales_trend WHERE month >= #{startMonth} AND month <= #{endMonth} ORDER BY month DESC")
    List<MonthlySalesTrendVO> selectMonthlySalesTrendByRange(
            @Param("startMonth") String startMonth,
            @Param("endMonth") String endMonth);
}
