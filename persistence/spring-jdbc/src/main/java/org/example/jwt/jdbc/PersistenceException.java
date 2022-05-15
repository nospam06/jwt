package org.example.jwt.jdbc;

public class PersistenceException extends RuntimeException {
    public PersistenceException(String msg, Throwable t) {
        super(msg, t);
    }
}
