package com.example.secerrordemo.app.security;

import com.example.secerrordemo.domain.security.IpAddress;
import com.example.secerrordemo.domain.security.SessionId;
import com.example.secerrordemo.domain.security.Username;
import jakarta.annotation.Nonnull;

import java.time.Duration;
import java.util.Optional;

public interface CurrentUser {

    @Nonnull
    Username username();

    @Nonnull
    Optional<Duration> passwordExpiresIn();

    @Nonnull
    SessionId sessionId();

    @Nonnull
    IpAddress ipAddress();
}
