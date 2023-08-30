package com.example.secerrordemo.app.security;

import com.example.secerrordemo.domain.security.IpAddress;
import com.example.secerrordemo.domain.security.SessionId;
import com.example.secerrordemo.domain.security.Username;
import com.example.secerrordemo.domain.security.log.AuthenticationLoggingService;
import com.example.secerrordemo.infra.tx.TxManager;
import jakarta.annotation.Nonnull;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.*;
import org.springframework.stereotype.Component;

@Component
class AuthenticationEventLogger {

    private final TxManager txManager;
    private final AuthenticationLoggingService authenticationLoggingService;

    AuthenticationEventLogger(TxManager txManager, AuthenticationLoggingService authenticationLoggingService) {
        this.txManager = txManager;
        this.authenticationLoggingService = authenticationLoggingService;
    }

    @EventListener
    public void onLogoutSuccess(LogoutSuccessEvent event) {
        var username = Username.fromString(event.getAuthentication().getName());
        var ipAddress = extractIpAddress(event);
        var sessionId = extractSessionId(event);
        txManager.runInNewTransaction(() -> authenticationLoggingService.logoutSuccess(username, sessionId, ipAddress));
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        var username = Username.fromString(event.getAuthentication().getName());
        var ipAddress = extractIpAddress(event);
        var sessionId = extractSessionId(event);
        txManager.runInNewTransaction(() -> authenticationLoggingService.authenticationSuccess(username, sessionId, ipAddress));
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        var username = Username.fromString(event.getAuthentication().getName());
        var ipAddress = extractIpAddress(event);
        var sessionId = extractSessionId(event);
        if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            txManager.runInNewTransaction(() -> authenticationLoggingService.badCredentials(username, sessionId, ipAddress));
        } else if (event instanceof AuthenticationFailureCredentialsExpiredEvent) {
            txManager.runInNewTransaction(() -> authenticationLoggingService.credentialsExpired(username, sessionId, ipAddress));
        } else if (event instanceof AuthenticationFailureExpiredEvent) {
            txManager.runInNewTransaction(() -> authenticationLoggingService.accountExpired(username, sessionId, ipAddress));
        } else if (event instanceof AuthenticationFailureLockedEvent) {
            txManager.runInNewTransaction(() -> authenticationLoggingService.accountLocked(username, sessionId, ipAddress));
        } else if (event instanceof AuthenticationFailureDisabledEvent) {
            txManager.runInNewTransaction(() -> authenticationLoggingService.accountDisabled(username, sessionId, ipAddress));
        } else {
            txManager.runInNewTransaction(() -> authenticationLoggingService.otherAuthenticationFailure(username, sessionId, ipAddress));
        }
    }

    private @Nonnull IpAddress extractIpAddress(@Nonnull AbstractAuthenticationEvent event) {
        return WebAuthenticationDetailsUtils.extractIpAddress(event.getAuthentication());
    }

    private @Nonnull SessionId extractSessionId(@Nonnull AbstractAuthenticationEvent event) {
        return WebAuthenticationDetailsUtils.extractSessionId(event.getAuthentication());
    }
}
