package com.carsales.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.carsales.dto.LoginRequest;
import com.carsales.dto.LoginResponse;
import com.carsales.entity.SysUser;
import com.carsales.exception.BusinessException;
import com.carsales.mapper.UserMapper;
import com.carsales.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务类
 * 处理用户认证相关业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 用户登录
     * 
     * @param request 登录请求
     * @return 登录响应（包含Token和用户信息）
     */
    public LoginResponse login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, request.getUsername());
        SysUser user = userMapper.selectOne(queryWrapper);

        // 验证用户是否存在
        if (user == null) {
            log.warn("登录失败：用户不存在 - {}", request.getUsername());
            throw new BusinessException(1001, "用户名或密码错误");
        }

        // 验证用户状态
        if (user.getStatus() == 0) {
            log.warn("登录失败：用户已禁用 - {}", request.getUsername());
            throw new BusinessException(1001, "用户已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("登录失败：密码错误 - {}", request.getUsername());
            throw new BusinessException(1001, "用户名或密码错误");
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 构建用户信息
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .build();

        log.info("用户登录成功 - 用户名: {}, 角色: {}", user.getUsername(), user.getRole());

        // 返回登录响应
        return LoginResponse.builder()
                .token(token)
                .userInfo(userInfo)
                .build();
    }

    /**
     * 用户登出
     * 注意：JWT是无状态的，登出主要在前端清除Token
     * 后端可以记录登出日志
     * 
     * @param username 用户名
     */
    public void logout(String username) {
        log.info("用户登出 - 用户名: {}", username);
        // JWT无状态，实际登出由前端清除Token完成
        // 这里可以添加登出日志记录或其他业务逻辑
    }

    /**
     * 根据用户名查询用户
     * 
     * @param username 用户名
     * @return 用户实体
     */
    public SysUser getUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        return userMapper.selectOne(queryWrapper);
    }

    /**
     * 根据ID查询用户
     * 
     * @param id 用户ID
     * @return 用户实体
     */
    public SysUser getUserById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 密码加密（用于创建用户时）
     * 
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 根据Token获取用户信息
     * 
     * @param token JWT Token
     * @return 登录响应（包含用户信息）
     */
    public LoginResponse getUserInfoByToken(String token) {
        // 从token中解析用户名
        String username = jwtUtil.getUsernameFromToken(token);
        if (username == null) {
            throw new BusinessException(1002, "Token无效或已过期");
        }

        // 查询用户
        SysUser user = getUserByUsername(username);
        if (user == null) {
            throw new BusinessException(1001, "用户不存在");
        }

        // 构建用户信息
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .build();

        return LoginResponse.builder()
                .token(token)
                .userInfo(userInfo)
                .build();
    }
}
