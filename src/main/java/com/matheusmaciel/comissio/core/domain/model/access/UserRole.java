package com.matheusmaciel.comissio.core.domain.model.access;

public enum UserRole {

    ADMIN("admin"),
    MANAGER("manager"),
    EMPLOYEE("employee");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
