package com.matheusmaciel.comissio.infra.exception.user;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException(String message) {
    super(message);
  }
}
