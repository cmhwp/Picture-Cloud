package com.photo.pcloudbackend.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    /**
     * 错误码
     */
    private final int Code;
    public BusinessException(int  Code, String message) {
        super(message);
        this.Code = Code;
    }
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.Code = errorCode.getCode();
    }
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.Code = errorCode.getCode();
    }

}
