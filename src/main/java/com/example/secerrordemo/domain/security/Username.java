package com.example.secerrordemo.domain.security;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public final class Username implements Serializable {

    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 100;

    private final String username;

    private Username(@Nonnull String username) {
        this.username = username;
    }

    public static @Nonnull Username fromString(@Nonnull String username) {
        return new Username(validate(username));
    }

    private static @Nonnull String validate(@Nonnull String username) {
        if (username.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Username is too short");
        }
        if (username.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Username is too long");
        }
        if (!StringUtils.isAlphanumeric(username)) {
            throw new IllegalArgumentException("Username must be alphanumeric");
        }
        return username;
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Username username1 = (Username) o;
        return Objects.equals(username, username1.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
