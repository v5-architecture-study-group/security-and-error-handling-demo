package com.example.secerrordemo.infra.session;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.util.concurrent.atomic.AtomicBoolean;

class SessionLoadingListener implements HttpSessionListener {

    private static final Logger log = LoggerFactory.getLogger(SessionLoadingListener.class);
    private final SessionSerde sessionSerde;
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);

    SessionLoadingListener(@Nonnull SessionSerde sessionSerde) {
        this.sessionSerde = sessionSerde;
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.trace("Session {} created", se.getSession().getId());
        if (sessionSerde.deserialize(se.getSession())) {
            log.debug("Session {} successfully deserialized from shared cache", se.getSession().getId());
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.trace("Session {} destroyed", se.getSession().getId());
        if (!shuttingDown.get()) {
            // Tomcat will automatically expire all its sessions upon shutdown. This could also happen if a
            // node goes down for maintenance, in which case other nodes in the cluster should take over the sessions.
            // Therefore, we don't delete any sessions from the shared cache if the node is shutting down.
            sessionSerde.delete(se.getSession());
        }
    }

    @EventListener
    public void contextClosed(ContextClosedEvent event) {
        log.trace("Context closed, it looks like we are shutting down");
        shuttingDown.set(true);
    }
}
