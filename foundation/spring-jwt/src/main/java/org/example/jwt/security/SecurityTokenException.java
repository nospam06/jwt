package org.example.jwt.security;

public class SecurityTokenException extends RuntimeException {
    public SecurityTokenException(String msg, Throwable t) {
        super(msg, t);
    }
}
