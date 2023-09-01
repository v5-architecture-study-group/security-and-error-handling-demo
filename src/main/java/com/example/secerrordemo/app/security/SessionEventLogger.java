package com.example.secerrordemo.app.security;

import com.example.secerrordemo.domain.security.SessionId;
import com.example.secerrordemo.domain.security.log.SessionLoggingService;
import com.example.secerrordemo.infra.tx.TxManager;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.session.AbstractSessionEvent;
import org.springframework.security.core.session.SessionCreationEvent;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionIdChangedEvent;
import org.springframework.stereotype.Component;

@Component
class SessionEventLogger {

    private final TxManager txManager;
    private final SessionLoggingService sessionLoggingService;

    SessionEventLogger(TxManager txManager, SessionLoggingService sessionLoggingService) {
        this.txManager = txManager;
        this.sessionLoggingService = sessionLoggingService;
    }

    @EventListener
    public void onSessionCreation(SessionCreationEvent sessionCreatedEvent) {
        txManager.runInNewTransaction(() -> sessionLoggingService.sessionCreated(extractSessionId(sessionCreatedEvent)));
    }

    @EventListener
    public void onSessionDestroyed(SessionDestroyedEvent sessionDestroyedEvent) {
        txManager.runInNewTransaction(() -> sessionLoggingService.sessionDestroyed(extractSessionId(sessionDestroyedEvent)));
    }

    @EventListener
    public void onSessionIdChanged(SessionIdChangedEvent sessionIdChangedEvent) {
        txManager.runInNewTransaction(() -> sessionLoggingService.sessionIdChanged(
                SessionId.fromString(sessionIdChangedEvent.getOldSessionId()),
                SessionId.fromString(sessionIdChangedEvent.getNewSessionId())));
    }

    private @Nonnull SessionId extractSessionId(@Nonnull AbstractSessionEvent event) {
        return SessionId.fromString(((HttpSession) event.getSource()).getId());
    }
}
