package com.matheusmaciel.comissio.infra.exception.employee;

public class EmployeeFoundException extends RuntimeException{
    public EmployeeFoundException(){
        super("Employee already exists");
    }
}
