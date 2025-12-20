package com.carsales.common;

import lombok.Getter;

/**
 * 响应状态码枚举
 * Response Status Code Enum
 */
@Getter
public enum ResultCode {

    // 成功
    SUCCESS(0, "操作成功"),

    // 1xxx: 认证授权错误
    AUTH_USERNAME_PASSWORD_ERROR(1001, "用户名或密码错误"),
    AUTH_TOKEN_INVALID(1002, "Token无效或已过期"),
    AUTH_PERMISSION_DENIED(1003, "权限不足"),

    // 2xxx: 参数验证错误
    PARAM_MISSING(2001, "必填参数缺失"),
    PARAM_FORMAT_ERROR(2002, "参数格式错误"),
    PARAM_VALUE_OUT_OF_RANGE(2003, "参数值超出范围"),

    // 3xxx: 业务逻辑错误
    BUSINESS_CAR_NOT_FOUND(3001, "车辆不存在"),
    BUSINESS_CAR_NOT_AVAILABLE(3002, "车辆状态不可售"),
    BUSINESS_CUSTOMER_EXISTS(3003, "客户已存在（手机号或身份证重复）"),
    BUSINESS_VIN_EXISTS(3004, "VIN已存在"),
    BUSINESS_CUSTOMER_HAS_ORDERS(3005, "客户有关联订单，无法删除"),
    BUSINESS_CAR_SOLD_CANNOT_DELETE(3006, "已售车辆无法删除"),

    // 4xxx: 数据库错误
    DB_UNIQUE_CONSTRAINT_VIOLATION(4001, "唯一约束冲突"),
    DB_FOREIGN_KEY_VIOLATION(4002, "外键约束冲突"),
    DB_CHECK_CONSTRAINT_VIOLATION(4003, "检查约束冲突"),
    DB_TRANSACTION_ROLLBACK(4004, "事务回滚"),

    // 5xxx: 系统错误
    SYSTEM_FILE_UPLOAD_ERROR(5001, "文件上传失败"),
    SYSTEM_FILE_PARSE_ERROR(5002, "文件解析失败"),
    SYSTEM_DB_CONNECTION_ERROR(5003, "数据库连接失败"),
    SYSTEM_UNKNOWN_ERROR(5004, "未知系统错误");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

}
