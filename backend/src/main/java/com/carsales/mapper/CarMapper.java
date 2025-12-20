package com.carsales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carsales.entity.CarInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车辆数据访问接口
 */
@Mapper
public interface CarMapper extends BaseMapper<CarInfo> {
    // MyBatis-Plus 提供了基础的 CRUD 方法
    // 复杂查询可以通过 LambdaQueryWrapper 在 Service 层实现
}
