package com.carsales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.carsales.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口
 */
@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
    // MyBatis-Plus 提供了基础的 CRUD 方法
    // 如需自定义查询，可在此添加方法
}
