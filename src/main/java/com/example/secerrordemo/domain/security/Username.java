package com.example.secerrordemo.domain.security;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public sealed abstract class Username implements Serializable permits Username.ValidUsername, Username.InvalidUsername {

    public static final int MIN_LENGTH = 3;
    public static final int MAX_LENGTH = 100;

    protected final String username;

    private Username(@Nonnull String username) {
        this.username = username;
    }

    public abstract boolean isValid();

    public static @Nonnull Username fromString(@Nonnull String username) {
        return isValid(username) ? new ValidUsername(username) : new InvalidUsername(username);
    }

    private static boolean isValid(@Nonnull String username) {
        return username.length() >= MIN_LENGTH && username.length() <= MAX_LENGTH && StringUtils.isAlphanumeric(username);
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

    public static final class ValidUsername extends Username {

        private ValidUsername(@Nonnull String username) {
            super(username);
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public String toString() {
            return username;
        }
    }

    public static final class InvalidUsername extends Username {

        private InvalidUsername(@Nonnull String username) {
            super(username);
        }

        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public String toString() {
            return "[INVALID USERNAME]";
        }
    }
}
