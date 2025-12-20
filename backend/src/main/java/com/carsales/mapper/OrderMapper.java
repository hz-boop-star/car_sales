package com.carsales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carsales.dto.OrderDetailVO;
import com.carsales.entity.SalesOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 订单数据访问接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<SalesOrder> {

    /**
     * 调用存储过程创建订单
     * 
     * @param salesUserId 销售员ID
     * @param customerId  客户ID
     * @param carId       车辆ID
     * @param actualPrice 实际成交价
     * @return 存储过程返回结果（包含 order_id, result_code, result_msg）
     */
    @Select("SELECT * FROM proc_create_order(#{salesUserId}, #{customerId}, #{carId}, #{actualPrice})")
    Map<String, Object> callCreateOrderProcedure(
            @Param("salesUserId") Long salesUserId,
            @Param("customerId") Long customerId,
            @Param("carId") Long carId,
            @Param("actualPrice") BigDecimal actualPrice);

    /**
     * 查询订单详情（包含关联的用户、客户、车辆信息）
     * 
     * @param orderId 订单ID
     * @return 订单详情
     */
    @Select("SELECT " +
            "o.id, o.order_no, o.original_price, o.actual_price, o.discount_amount, " +
            "o.order_date, o.status, o.remark, o.create_time, o.update_time, " +
            "o.sales_user_id, u.real_name AS sales_user_name, u.phone AS sales_user_phone, " +
            "o.customer_id, c.name AS customer_name, c.phone AS customer_phone, c.id_card AS customer_id_card, " +
            "o.car_id, car.vin AS car_vin, car.brand AS car_brand, car.model AS car_model, " +
            "car.color AS car_color, car.year AS car_year " +
            "FROM sales_order o " +
            "INNER JOIN sys_user u ON o.sales_user_id = u.id " +
            "INNER JOIN customer c ON o.customer_id = c.id " +
            "INNER JOIN car_info car ON o.car_id = car.id " +
            "WHERE o.id = #{orderId}")
    OrderDetailVO selectOrderDetailById(@Param("orderId") Long orderId);
}
