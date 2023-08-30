package com.example.secerrordemo.domain.security.log;

import com.example.secerrordemo.domain.security.SessionId;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
class SessionLoggingServiceImpl implements SessionLoggingService {

    private final Clock clock;
    private final SessionLogEntryRepository repository;

    SessionLoggingServiceImpl(Clock clock, SessionLogEntryRepository repository) {
        this.clock = clock;
        this.repository = repository;
    }

    @Override
    public void sessionCreated(@Nonnull SessionId sessionId) {
        repository.save(new SessionLogEntry(clock.instant(), sessionId, SessionLogEntryType.CREATED));
    }

    @Override
    public void sessionDestroyed(@Nonnull SessionId sessionId) {
        repository.save(new SessionLogEntry(clock.instant(), sessionId, SessionLogEntryType.DESTROYED));
    }

    @Override
    public void sessionDeleted(@Nonnull SessionId sessionId) {
        repository.save(new SessionLogEntry(clock.instant(), sessionId, SessionLogEntryType.DESTROYED_DELETED));
    }

    @Override
    public void sessionExpired(@Nonnull SessionId sessionId) {
        repository.save(new SessionLogEntry(clock.instant(), sessionId, SessionLogEntryType.DESTROYED_EXPIRED));
    }
}
