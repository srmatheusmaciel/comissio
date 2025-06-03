package com.matheusmaciel.comissio.infra.exception.performedService;

public class UpdatePerformedServiceException extends RuntimeException {
    public UpdatePerformedServiceException(String message) {
        super(message);
    }

    public UpdatePerformedServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
