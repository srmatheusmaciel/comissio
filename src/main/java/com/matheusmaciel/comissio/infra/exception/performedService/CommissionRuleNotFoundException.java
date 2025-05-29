package com.matheusmaciel.comissio.infra.exception.performedService;

public class CommissionRuleNotFoundException extends RuntimeException {
    public CommissionRuleNotFoundException(String message) {
        super(message);
    }
}
