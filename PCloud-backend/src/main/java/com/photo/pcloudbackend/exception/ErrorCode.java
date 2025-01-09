package com.photo.pcloudbackend.exception;

import lombok.Getter;
@Getter
public enum ErrorCode {
    SUCCESS(0, "ok"),
    PARAMETER_ERROR(40000, "参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NOT_PERMISSION_ERROR(40300, "无权限"),
    NOT_FOUND_ERROR(40400, "未找到"),
    INTERNAL_SERVER_ERROR(50000, "服务器内部错误"),
    OPERATION_ERROR(50001, "操作失败");
    private final int code;
    private final String message;
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
