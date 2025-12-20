package com.carsales.dto;

import lombok.Data;

/**
 * 订单查询请求DTO
 */
@Data
public class OrderQueryRequest {

    /**
     * 开始日期（格式：yyyy-MM-dd）
     */
    private String startDate;

    /**
     * 结束日期（格式：yyyy-MM-dd）
     */
    private String endDate;

    /**
     * 销售员ID
     */
    private Long salesUserId;

    /**
     * 订单状态：1-已完成, 2-已取消
     */
    private Integer status;

    /**
     * 页码（默认1）
     */
    private Integer pageNum = 1;

    /**
     * 每页大小（默认10）
     */
    private Integer pageSize = 10;
}
