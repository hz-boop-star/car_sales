package com.carsales.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Result类单元测试
 * Result Class Unit Test
 */
class ResultTest {

    @Test
    void testSuccessWithoutData() {
        Result<Void> result = Result.success();

        assertThat(result.getCode()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("操作成功");
        assertThat(result.getData()).isNull();
        assertThat(result.getTimestamp()).isNotNull();
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void testSuccessWithData() {
        String data = "test data";
        Result<String> result = Result.success(data);

        assertThat(result.getCode()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo("操作成功");
        assertThat(result.getData()).isEqualTo(data);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void testSuccessWithCustomMessage() {
        String message = "自定义成功消息";
        String data = "test data";
        Result<String> result = Result.success(message, data);

        assertThat(result.getCode()).isEqualTo(0);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getData()).isEqualTo(data);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void testErrorWithCodeAndMessage() {
        Integer code = 1001;
        String message = "错误消息";
        Result<Void> result = Result.error(code, message);

        assertThat(result.getCode()).isEqualTo(code);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.getData()).isNull();
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void testErrorWithResultCode() {
        Result<Void> result = Result.error(ResultCode.AUTH_USERNAME_PASSWORD_ERROR);

        assertThat(result.getCode()).isEqualTo(1001);
        assertThat(result.getMessage()).isEqualTo("用户名或密码错误");
        assertThat(result.getData()).isNull();
        assertThat(result.isSuccess()).isFalse();
    }

    @Test
    void testErrorWithResultCodeAndCustomMessage() {
        String customMessage = "自定义错误消息";
        Result<Void> result = Result.error(ResultCode.AUTH_USERNAME_PASSWORD_ERROR, customMessage);

        assertThat(result.getCode()).isEqualTo(1001);
        assertThat(result.getMessage()).isEqualTo(customMessage);
        assertThat(result.getData()).isNull();
        assertThat(result.isSuccess()).isFalse();
    }

}
