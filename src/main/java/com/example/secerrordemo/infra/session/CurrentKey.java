package com.example.secerrordemo.infra.session;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

final class CurrentKey {

    private static final Logger log = LoggerFactory.getLogger(CurrentKey.class);
    private static final ThreadLocal<SessionKey> CURRENT_KEY = new ThreadLocal<>();

    private CurrentKey() {
    }

    public static @Nonnull Optional<SessionKey> current() {
        return Optional.ofNullable(CURRENT_KEY.get());
    }

    public static void setCurrent(@Nullable SessionKey key) {
        if (key != null) {
            log.trace("Setting current key to {}", key);
            CURRENT_KEY.set(key);
        } else {
            log.trace("Removing current key");
            CURRENT_KEY.remove();
        }
    }
}
