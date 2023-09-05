package com.example.secerrordemo.infra.session;

import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.*;

class SessionStoringFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SessionStoringFilter.class);
    private static final Duration HOLD_ON_TO_OLD_SESSIONS_FOR = Duration.ofMinutes(5);
    private final SessionKeyResolver sessionKeyResolver;
    private final SessionSerde sessionSerde;
    private final ConcurrentMap<String, Instant> migratedOldSessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService oldSessionCleanUpJob = Executors.newSingleThreadScheduledExecutor();

    SessionStoringFilter(@Nonnull SessionKeyResolver sessionKeyResolver, @Nonnull SessionSerde sessionSerde) {
        this.sessionKeyResolver = sessionKeyResolver;
        this.sessionSerde = sessionSerde;
        oldSessionCleanUpJob.scheduleWithFixedDelay(this::cleanUpMigratedOldSessions, 1, 1, TimeUnit.MINUTES);
    }

    private void cleanUpMigratedOldSessions() {
        // We have to remember to clean up migratedOldSessions, otherwise it could fill up over time.
        var entries = Set.copyOf(migratedOldSessions.entrySet());
        var evictEntriesCreatedBefore = Instant.now().minus(HOLD_ON_TO_OLD_SESSIONS_FOR);
        entries.forEach(entry -> {
            if (entry.getValue().isBefore(evictEntriesCreatedBefore)) {
                log.trace("Evicting migrated session {}", entry.getKey());
                migratedOldSessions.remove(entry.getKey());
            }
        });
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        if (isActuatorRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        log.trace("Starting processing of request {}, requested session ID is {} (valid: {})", toString(request), request.getRequestedSessionId(), request.isRequestedSessionIdValid());

        // We could receive multiple requests (heartbeats, pushes) with the old session ID. We only want to process the first
        // of them and force the others to be retried with the new session ID.
        var requestedSessionId = request.getRequestedSessionId();
        if (requestedSessionId != null && !request.isRequestedSessionIdValid()) {
            if (migratedOldSessions.putIfAbsent(requestedSessionId, Instant.now()) == null) {
                log.trace("Received a request for a session ({}} that has already been migrated, returning error", requestedSessionId);
                response.sendError(400, "Re-send your request with a fresh session ID");
                return;
            } else {
                log.trace("Marked old session {} as migrated ({})", requestedSessionId, migratedOldSessions.size());
            }
        }

        sessionKeyResolver.setCurrentKey(request);
        try {
            filterChain.doFilter(request, response);
            sessionKeyResolver.storeCurrentKey(request, response);
            var session = request.getSession(false);
            if (session != null && isUIDLRequest(request)) {
                log.trace("Serializing HTTP session {} after processing request {}", session.getId(), toString(request));
                sessionSerde.serialize(session);
            }
        } finally {
            CurrentKey.setCurrent(null);
        }
    }

    @Override
    public void destroy() {
        oldSessionCleanUpJob.shutdown();
    }

    private boolean isActuatorRequest(@Nonnull HttpServletRequest request) {
        var path = request.getRequestURI().substring(request.getContextPath().length());
        return path.toLowerCase().startsWith("/actuator/");
    }

    private boolean isUIDLRequest(@Nonnull HttpServletRequest request) {
        return HandlerHelper.RequestType.UIDL.getIdentifier()
                .equals(request.getParameter(
                        ApplicationConstants.REQUEST_TYPE_PARAMETER));
    }

    private @Nonnull String toString(@Nonnull HttpServletRequest request) {
        var sb = new StringBuilder();
        sb.append(request.getMethod());
        sb.append(" ");
        sb.append(request.getRequestURI());
        var q = request.getQueryString();
        if (q != null) {
            sb.append("?").append(q);
        }
        return sb.toString();
    }
}
