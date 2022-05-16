package org.example.jwt.logic;

public class UserException extends RuntimeException {
    public UserException(String msg, Throwable t) {
        super(msg, t);
    }
}
