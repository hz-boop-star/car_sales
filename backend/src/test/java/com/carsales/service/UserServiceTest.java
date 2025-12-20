package com.carsales.service;

import com.carsales.dto.LoginRequest;
import com.carsales.dto.LoginResponse;
import com.carsales.entity.SysUser;
import com.carsales.exception.BusinessException;
import com.carsales.mapper.UserMapper;
import com.carsales.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 * 测试用户认证相关功能
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户认证服务测试")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private SysUser testUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        testUser = new SysUser();
        testUser.setId(1001L);
        testUser.setUsername("admin");
        testUser.setPassword("$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi"); // BCrypt hash of "123456"
        testUser.setRealName("系统管理员");
        testUser.setRole("ADMIN");
        testUser.setStatus(1);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("123456");
    }

    @Test
    @DisplayName("测试有效凭证登录成功")
    void testLoginWithValidCredentials() {
        // Given
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches("123456", testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(testUser.getId(), testUser.getUsername(), testUser.getRole()))
                .thenReturn("mock-jwt-token");

        // When
        LoginResponse response = userService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock-jwt-token");
        assertThat(response.getUserInfo()).isNotNull();
        assertThat(response.getUserInfo().getId()).isEqualTo(1001L);
        assertThat(response.getUserInfo().getUsername()).isEqualTo("admin");
        assertThat(response.getUserInfo().getRealName()).isEqualTo("系统管理员");
        assertThat(response.getUserInfo().getRole()).isEqualTo("ADMIN");

        // Verify
        verify(userMapper, times(1)).selectOne(any());
        verify(passwordEncoder, times(1)).matches("123456", testUser.getPassword());
        verify(jwtUtil, times(1)).generateToken(testUser.getId(), testUser.getUsername(), testUser.getRole());
    }

    @Test
    @DisplayName("测试用户不存在时登录失败")
    void testLoginWithNonExistentUser() {
        // Given
        when(userMapper.selectOne(any())).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户名或密码错误");

        // Verify
        verify(userMapper, times(1)).selectOne(any());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generateToken(any(), any(), any());
    }

    @Test
    @DisplayName("测试密码错误时登录失败")
    void testLoginWithInvalidPassword() {
        // Given
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        loginRequest.setPassword("wrongpassword");

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户名或密码错误");

        // Verify
        verify(userMapper, times(1)).selectOne(any());
        verify(passwordEncoder, times(1)).matches("wrongpassword", testUser.getPassword());
        verify(jwtUtil, never()).generateToken(any(), any(), any());
    }

    @Test
    @DisplayName("测试禁用用户登录失败")
    void testLoginWithDisabledUser() {
        // Given
        testUser.setStatus(0); // 禁用状态
        when(userMapper.selectOne(any())).thenReturn(testUser);

        // When & Then
        assertThatThrownBy(() -> userService.login(loginRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("用户已被禁用");

        // Verify
        verify(userMapper, times(1)).selectOne(any());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtUtil, never()).generateToken(any(), any(), any());
    }

    @Test
    @DisplayName("测试根据用户名查询用户")
    void testGetUserByUsername() {
        // Given
        when(userMapper.selectOne(any())).thenReturn(testUser);

        // When
        SysUser result = userService.getUserByUsername("admin");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("admin");
        assertThat(result.getId()).isEqualTo(1001L);

        // Verify
        verify(userMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("测试根据ID查询用户")
    void testGetUserById() {
        // Given
        when(userMapper.selectById(1001L)).thenReturn(testUser);

        // When
        SysUser result = userService.getUserById(1001L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1001L);
        assertThat(result.getUsername()).isEqualTo("admin");

        // Verify
        verify(userMapper, times(1)).selectById(1001L);
    }

    @Test
    @DisplayName("测试密码加密")
    void testEncodePassword() {
        // Given
        String rawPassword = "123456";
        String encodedPassword = "$2a$10$encodedPasswordHash";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        // When
        String result = userService.encodePassword(rawPassword);

        // Then
        assertThat(result).isEqualTo(encodedPassword);

        // Verify
        verify(passwordEncoder, times(1)).encode(rawPassword);
    }
}
