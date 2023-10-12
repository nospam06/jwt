package org.example.jwt.jpa;

public class PersistenceException extends RuntimeException {
    public PersistenceException(String msg, Throwable t) {
        super(msg, t);
    }
}
