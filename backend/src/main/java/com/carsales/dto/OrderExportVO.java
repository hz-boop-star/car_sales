package com.carsales.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 订单导出 VO
 * 用于 Excel 导出
 */
@Data
public class OrderExportVO {

    @ExcelProperty("订单编号")
    private String orderNo;

    @ExcelProperty("订单日期")
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate orderDate;

    @ExcelProperty("销售员")
    private String salesUserName;

    @ExcelProperty("客户姓名")
    private String customerName;

    @ExcelProperty("客户手机")
    private String customerPhone;

    @ExcelProperty("车辆品牌")
    private String carBrand;

    @ExcelProperty("车辆型号")
    private String carModel;

    @ExcelProperty("车架号")
    private String carVin;

    @ExcelProperty("原价")
    private BigDecimal originalPrice;

    @ExcelProperty("成交价")
    private BigDecimal actualPrice;

    @ExcelProperty("优惠金额")
    private BigDecimal discountAmount;

    @ExcelProperty("订单状态")
    private String statusText;
}
