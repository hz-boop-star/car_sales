package com.carsales.dto;

import lombok.Data;

/**
 * 客户查询请求DTO
 */
@Data
public class CustomerQueryRequest {

    /**
     * 客户姓名（模糊匹配）
     */
    private String name;

    /**
     * 手机号（精确匹配）
     */
    private String phone;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;
}
