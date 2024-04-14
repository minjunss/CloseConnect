package com.CloseConnect.closeconnect.global.exception;

import lombok.Getter;

public class BusinessException extends RuntimeException{
    @Getter
    private final ExceptionCode exceptionCode;
    private final String field;

    public BusinessException(ExceptionCode exceptionCode, String field) {
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
        this.field = field;
    }

    @Override
    public String getMessage() {
        return exceptionCode.getMessage() + "value: " + field;
    }
}
