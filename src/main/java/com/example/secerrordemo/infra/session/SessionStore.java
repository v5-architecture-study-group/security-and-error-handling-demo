package com.example.secerrordemo.infra.session;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public interface SessionStore {

    void save(@Nonnull SessionKey sessionKey, @Nonnull SessionSaveJob saveJob);

    void load(@Nonnull SessionKey sessionKey, @Nonnull SessionAttributeSink sink);

    void delete(@Nonnull SessionKey sessionKey);

    @FunctionalInterface
    interface SessionAttributeSink {
        void write(@Nonnull String attributeName, @Nullable Object attributeValue);
    }

    @FunctionalInterface
    interface SessionSaveJob {
        void writeAttributes(@Nonnull SessionAttributeSink sink);
    }
}
