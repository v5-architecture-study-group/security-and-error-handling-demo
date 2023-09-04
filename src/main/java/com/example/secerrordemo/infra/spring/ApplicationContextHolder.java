package com.example.secerrordemo.infra.spring;

import jakarta.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    private final static AtomicReference<ApplicationContext> APPLICATION_CONTEXT = new AtomicReference<>();

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        if (!APPLICATION_CONTEXT.compareAndSet(null, applicationContext)) {
            throw new IllegalStateException("ApplicationContext has already been set");
        }
    }

    public static @Nonnull ApplicationContext getApplicationContext() {
        var appContext = APPLICATION_CONTEXT.get();
        if (appContext == null) {
            throw new IllegalStateException("ApplicationContext has not been set yet");
        }
        return appContext;
    }
}
