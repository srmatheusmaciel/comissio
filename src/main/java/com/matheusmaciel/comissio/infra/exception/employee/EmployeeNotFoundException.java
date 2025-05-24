package com.matheusmaciel.comissio.infra.exception.employee;

public class EmployeeNotFoundException extends RuntimeException{

  public EmployeeNotFoundException(String message) {
    super(message);
  }

}
