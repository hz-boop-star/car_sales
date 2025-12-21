package com.carsales.controller;

import com.carsales.annotation.OperationLog;
import com.carsales.common.Result;
import com.carsales.dto.LoginRequest;
import com.carsales.dto.LoginResponse;
import com.carsales.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供用户登录、登出接口
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 用户登录
     * 
     * @param request 登录请求
     * @return 登录响应（包含Token和用户信息）
     */
    @PostMapping("/login")
    @OperationLog("用户登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("收到登录请求 - 用户名: {}", request.getUsername());
        LoginResponse response = userService.login(request);
        return Result.success(response);
    }

    /**
     * 用户登出
     * 
     * @param username 用户名（从请求头或参数获取）
     * @return 成功响应
     */
    @PostMapping("/logout")
    public Result<String> logout(@RequestParam(required = false) String username) {
        log.info("收到登出请求 - 用户名: {}", username);
        if (username != null) {
            userService.logout(username);
        }
        return Result.success("登出成功");
    }

    /**
     * 获取当前用户信息
     * 
     * @param token JWT Token
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<LoginResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        log.info("获取用户信息");
        // 从token中解析用户信息
        String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        LoginResponse response = userService.getUserInfoByToken(actualToken);
        return Result.success(response);
    }

    /**
     * 获取销售员列表
     * 
     * @return 销售员列表
     */
    @GetMapping("/salespersons")
    public Result<java.util.List<com.carsales.entity.SysUser>> getSalespersons() {
        log.info("获取销售员列表");
        java.util.List<com.carsales.entity.SysUser> salespersons = userService.getSalespersons();
        return Result.success(salespersons);
    }
}
