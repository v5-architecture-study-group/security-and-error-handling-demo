package com.example.secerrordemo.app.security;

import com.example.secerrordemo.domain.security.IpAddress;
import com.example.secerrordemo.domain.security.SessionId;
import com.example.secerrordemo.domain.security.UserAccountRepository;
import com.example.secerrordemo.domain.security.Username;
import com.example.secerrordemo.infra.tx.TxManager;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.util.Optional;

@Component
class CurrentUserImpl implements CurrentUser {

    private final Clock clock;
    private final TxManager txManager;
    private final UserAccountRepository userAccountRepository;

    CurrentUserImpl(Clock clock, TxManager txManager, UserAccountRepository userAccountRepository) {
        this.clock = clock;
        this.txManager = txManager;
        this.userAccountRepository = userAccountRepository;
    }

    @Nonnull
    @Override
    public Username username() {
        return Optional.ofNullable(getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .map(Username::fromString)
                .orElseThrow(() -> new AccessDeniedException("No current user"));
    }

    @Nonnull
    @Override
    public Optional<Duration> passwordExpiresIn() {
        return txManager.callInNewTransaction(() -> userAccountRepository
                .findByUsername(username())
                .flatMap(account -> account.passwordExpiresIn(clock)));
    }

    @Nonnull
    @Override
    public SessionId sessionId() {
        return WebAuthenticationDetailsUtils.extractSessionId(getAuthentication());
    }

    @Nonnull
    @Override
    public IpAddress ipAddress() {
        return WebAuthenticationDetailsUtils.extractIpAddress(getAuthentication());
    }

    @Override
    public boolean hasAuthority(@Nonnull String authority) {
        return Optional.ofNullable(getAuthentication())
                .map(auth -> auth.getAuthorities().stream().anyMatch(ga -> ga.getAuthority().equals(authority)))
                .orElse(false);
    }

    private @Nullable Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
