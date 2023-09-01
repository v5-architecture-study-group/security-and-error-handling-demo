package com.example.secerrordemo.infra.session;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public interface SessionStore {

    void save(@Nonnull String sessionId, @Nonnull SessionSaveJob saveJob);

    void load(@Nonnull String sessionId, @Nonnull SessionAttributeSink sink);

    void delete(@Nonnull String sessionId);

    @FunctionalInterface
    interface SessionAttributeSink {
        void write(@Nonnull String attributeName, @Nullable Object attributeValue);
    }

    @FunctionalInterface
    interface SessionSaveJob {
        void writeAttributes(@Nonnull SessionAttributeSink sink);
    }
}
