package com.matheusmaciel.comissio.infra.exception;

public class UserFoundException extends RuntimeException {

    public UserFoundException(){
        super("User already exists");
    }
}
