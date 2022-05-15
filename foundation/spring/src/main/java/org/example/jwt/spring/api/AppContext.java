package org.example.jwt.spring.api;

public interface AppContext {
    <T> T findImplementation(Class<T> cls);

    <T> T findImplementation(Class<T> cls, String beanId);
}
