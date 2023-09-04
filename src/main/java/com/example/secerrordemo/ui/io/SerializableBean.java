package com.example.secerrordemo.ui.io;

import com.example.secerrordemo.infra.spring.ApplicationContextHolder;
import jakarta.annotation.Nonnull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public final class SerializableBean<T> implements Serializable {

    private final Class<T> beanType;
    private transient T bean;

    private SerializableBean(@Nonnull Class<T> serviceType) {
        this.beanType = Objects.requireNonNull(serviceType);
        this.bean = ApplicationContextHolder.getApplicationContext().getBean(beanType);
    }

    public static <T> @Nonnull SerializableBean<T> ofType(@Nonnull Class<T> serviceType) {
        return new SerializableBean<>(serviceType);
    }

    public @Nonnull T get() {
        return bean;
    }

    @Serial
    private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
        in.defaultReadObject();
        bean = ApplicationContextHolder.getApplicationContext().getBean(beanType);
    }
}
