package com.example.secerrordemo.domain.security;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class ReadOnceEncodedPassword { // Not serializable by design

    public static final int MAX_LENGTH = 100;

    private final AtomicReference<String> encodedPassword;

    private ReadOnceEncodedPassword(@Nullable String encodedPassword) {
        if (encodedPassword != null && encodedPassword.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Encoded password is too long");
        }
        this.encodedPassword = new AtomicReference<>(encodedPassword);
    }

    public @Nonnull String unwrap() {
        return Optional.ofNullable(encodedPassword.getAndSet(null)).orElseThrow(() -> new IllegalStateException("Password has already been read"));
    }

    public static @Nonnull ReadOnceEncodedPassword empty() {
        return new ReadOnceEncodedPassword(null);
    }

    public static @Nonnull ReadOnceEncodedPassword wrap(@Nullable String encodedPassword) {
        return new ReadOnceEncodedPassword(encodedPassword);
    }
}
