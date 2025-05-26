package com.matheusmaciel.comissio.infra.exception;

public class BusinessException extends  RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
