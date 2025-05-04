package com.matheusmaciel.comissio.infra.exception;

public class EmployeeFoundException extends RuntimeException{
    public EmployeeFoundException(){
        super("Employee already exists");
    }
}
