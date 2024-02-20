package com.example.fulbrincjava.exceptions;

public class UserAlreadyExists extends Throwable {
    public UserAlreadyExists(String message) {
        super(message);
    }
}
