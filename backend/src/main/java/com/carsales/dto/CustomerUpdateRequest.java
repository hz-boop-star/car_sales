package com.carsales.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 客户更新请求DTO
 */
@Data
public class CustomerUpdateRequest {

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long id;

    /**
     * 客户姓名
     */
    @Size(max = 50, message = "客户姓名长度不能超过50个字符")
    private String name;

    /**
     * 手机号（11位）
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 身份证号（18位）
     */
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$", message = "身份证号格式不正确")
    private String idCard;

    /**
     * 性别：M-男, F-女
     */
    @Pattern(regexp = "^[MF]$", message = "性别只能为M或F")
    private String gender;

    /**
     * 地址
     */
    @Size(max = 200, message = "地址长度不能超过200个字符")
    private String address;
}
