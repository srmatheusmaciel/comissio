package com.matheusmaciel.comissio.infra.exception.serviceType;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
