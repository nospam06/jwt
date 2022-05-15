package org.example.jwt.spring.service;

import lombok.RequiredArgsConstructor;
import org.example.jwt.spring.api.AppContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppContextImpl implements AppContext {
    private final ApplicationContext context;

    @Override public <T> T findImplementation(Class<T> cls) {
        return context.getBean(cls);
    }
    
    @Override public <T> T findImplementation(Class<T> cls, String beanId) {
        return context.getBean(beanId, cls);
    }
}
