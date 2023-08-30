package com.example.secerrordemo.app.security;

import com.example.secerrordemo.domain.security.SessionId;
import com.example.secerrordemo.domain.security.log.SessionLoggingService;
import com.example.secerrordemo.infra.tx.TxManager;
import org.springframework.context.event.EventListener;
import org.springframework.session.events.SessionCreatedEvent;
import org.springframework.session.events.SessionDeletedEvent;
import org.springframework.session.events.SessionDestroyedEvent;
import org.springframework.session.events.SessionExpiredEvent;
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
    public void onSessionCreated(SessionCreatedEvent sessionCreatedEvent) {
        txManager.runInNewTransaction(() -> sessionLoggingService.sessionCreated(SessionId.fromString(sessionCreatedEvent.getSessionId())));
    }

    @EventListener
    public void onSessionDestroyed(SessionDestroyedEvent sessionDestroyedEvent) {
        var sessionId = SessionId.fromString(sessionDestroyedEvent.getSessionId());
        if (sessionDestroyedEvent instanceof SessionDeletedEvent) {
            txManager.runInNewTransaction(() -> sessionLoggingService.sessionDeleted(sessionId));
        } else if (sessionDestroyedEvent instanceof SessionExpiredEvent) {
            txManager.runInNewTransaction(() -> sessionLoggingService.sessionExpired(sessionId));
        } else {
            txManager.runInNewTransaction(() -> sessionLoggingService.sessionDestroyed(sessionId));
        }
    }
}
