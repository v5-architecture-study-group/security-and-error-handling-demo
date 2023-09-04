package com.example.secerrordemo.infra.session;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.util.concurrent.atomic.AtomicInteger;

class SessionSerde {

    private static final Logger log = LoggerFactory.getLogger(SessionSerde.class);
    private final SessionKeyResolver sessionKeyResolver;
    private final SessionStore sessionStore;
    private final AtomicInteger serdeOperations = new AtomicInteger(0);

    SessionSerde(@Nonnull SessionKeyResolver sessionKeyResolver, @Nonnull SessionStore sessionStore) {
        this.sessionKeyResolver = sessionKeyResolver;
        this.sessionStore = sessionStore;
    }

    public void serialize(@Nonnull HttpSession session) {
        log.trace("Attempting to serialize HTTP session {}", session.getId());
        serdeOperations.incrementAndGet();
        try {
            sessionKeyResolver.getCurrentKey(session).ifPresent(key -> sessionStore.save(key,
                    sink -> session.getAttributeNames().asIterator().forEachRemaining(
                            name -> sink.write(name, session.getAttribute(name)))));
        } finally {
            serdeOperations.decrementAndGet();
        }
    }

    public void deserialize(@Nonnull HttpSession session) {
        log.trace("Attempting to deserialize HTTP session {}", session.getId());
        serdeOperations.incrementAndGet();
        try {
            sessionKeyResolver.getCurrentKey(session).ifPresent(key -> sessionStore.load(key, session::setAttribute));
        } finally {
            serdeOperations.decrementAndGet();
        }
    }

    public void delete(@Nonnull HttpSession session) {
        log.trace("Attempting to delete data for HTTP session {}", session.getId());
        serdeOperations.incrementAndGet();
        try {
            sessionKeyResolver.getCurrentKey(session).ifPresent(sessionStore::delete);
        } finally {
            serdeOperations.decrementAndGet();
        }
    }

    @SuppressWarnings("BusyWait")
    @EventListener
    public void contextClosed(ContextClosedEvent event) throws Exception {
        while (serdeOperations.get() > 0) {
            log.trace("Waiting for serde operations to complete before shutting down");
            Thread.sleep(100);
        }
    }
}
