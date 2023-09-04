package com.example.secerrordemo.infra.session;

import jakarta.annotation.Nonnull;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public final class SessionKey implements Serializable {

    private static final int KEY_LENGTH_BYTES = 16; // 128 bits, https://owasp.org/www-community/vulnerabilities/Insufficient_Session-ID_Length
    private static final SecureRandom secureRandom;

    static {
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("Could not initialize random number generator", ex);
        }
    }

    private final String key;

    private SessionKey(@Nonnull String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }

    public static @Nonnull SessionKey fromString(@Nonnull String key) {
        return new SessionKey(Objects.requireNonNull(key));
    }

    public static @Nonnull SessionKey randomKey() {
        var key = new byte[KEY_LENGTH_BYTES];
        secureRandom.nextBytes(key);
        return new SessionKey(Base64.getUrlEncoder().encodeToString(key));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionKey that = (SessionKey) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
