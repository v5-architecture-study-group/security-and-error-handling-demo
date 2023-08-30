package com.example.secerrordemo.domain.security;

import jakarta.annotation.Nonnull;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public final class ReadOnceRawPassword { // Not serializable by design

    public static final int MAX_LENGTH = 72; // Because bcrypt. Actually, the bcrypt limit is 72 bytes. Since we are
    // working with Unicode strings, a string of length 72 might be larger than 72 bytes.

    private final AtomicReference<StringBuffer> password;

    private ReadOnceRawPassword(@Nonnull CharSequence password) {
        if (password.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Password is too long (stupid, we know)");
        }
        this.password = new AtomicReference<>(new StringBuffer(password));
    }

    public static @Nonnull ReadOnceRawPassword wrap(@Nonnull CharSequence rawPassword) {
        return new ReadOnceRawPassword(rawPassword);
    }

    public <R> R map(@Nonnull Function<CharSequence, R> rawPasswordMapper) {
        var pw = password.getAndSet(null);
        if (pw == null) {
            throw new IllegalStateException("Password has already been consumed");
        }
        try {
            return rawPasswordMapper.apply(pw);
        } finally {
            // Finally write over the old password in memory
            for (var i = 0; i < pw.length(); ++i) {
                pw.setCharAt(i, '0');
            }
        }
    }
}
