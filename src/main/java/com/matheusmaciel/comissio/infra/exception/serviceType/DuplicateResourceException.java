package com.matheusmaciel.comissio.infra.exception.serviceType;

public class DuplicateResourceException extends RuntimeException{
    public DuplicateResourceException(String message){ super(message); }
}
