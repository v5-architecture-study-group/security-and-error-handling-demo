package com.example.secerrordemo.domain.security;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public final class SessionId implements Serializable {

    public static final int MAX_LENGTH = 100;
    public static final SessionId UNKNOWN = new SessionId("");

    private final String sessionId;

    private SessionId(@Nonnull String sessionId) {
        this.sessionId = sessionId;
    }

    public static @Nonnull SessionId fromString(@Nonnull String sessionId) {
        return new SessionId(validate(sessionId));
    }

    private static @Nonnull String validate(@Nonnull String sessionId) {
        if (sessionId.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("SessionID too long");
        }
        if (!sessionId.isEmpty() && !StringUtils.containsOnly(sessionId, "1234567890abcdefABCDEF-")) {
            throw new IllegalArgumentException("SessionID contains invalid characters");
        }
        return sessionId;
    }

    @Override
    public String toString() {
        return sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionId sessionId1 = (SessionId) o;
        return Objects.equals(sessionId, sessionId1.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId);
    }
}
