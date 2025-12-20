package com.carsales.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 车辆查询请求DTO
 */
@Data
public class CarQueryRequest {

    /**
     * 品牌（精确匹配）
     */
    private String brand;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 最高价格
     */
    private BigDecimal maxPrice;

    /**
     * 状态：0-在库, 1-锁定, 2-已售
     */
    private Integer status;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
