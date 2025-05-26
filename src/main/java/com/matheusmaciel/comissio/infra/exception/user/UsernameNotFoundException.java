package com.matheusmaciel.comissio.infra.exception.user;

public class UsernameNotFoundException extends RuntimeException {
  public UsernameNotFoundException() {
    super("Username not found");
  }
}
