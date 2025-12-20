package com.carsales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carsales.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户数据访问接口
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
    // MyBatis-Plus 提供了基础的 CRUD 方法
    // 复杂查询可以通过 LambdaQueryWrapper 在 Service 层实现
}
