package com.example.secerrordemo.domain.security.log;

import com.example.secerrordemo.domain.security.SessionId;
import com.example.secerrordemo.domain.security.SessionIdAttributeConverter;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "session_log")
class SessionLogEntry extends AbstractPersistable<Long> {

    @Column(nullable = false, name = "ts")
    private Instant timestamp;
    @Column(nullable = false, length = SessionId.MAX_LENGTH, name = "session_id")
    @Convert(converter = SessionIdAttributeConverter.class)
    private SessionId sessionId;
    @Column(nullable = true, length = SessionId.MAX_LENGTH, name = "old_session_id")
    @Convert(converter = SessionIdAttributeConverter.class)
    private SessionId oldSessionId;

    @Column(nullable = false, length = 100, name = "entry_type")
    @Enumerated(EnumType.STRING)
    private SessionLogEntryType entryType;

    protected SessionLogEntry() { // Required by Hibernate
    }

    public SessionLogEntry(@Nonnull Instant timestamp,
                           @Nonnull SessionId sessionId,
                           @Nonnull SessionLogEntryType entryType) {
        this(timestamp, sessionId, null, entryType);
    }

    public SessionLogEntry(@Nonnull Instant timestamp,
                           @Nonnull SessionId sessionId,
                           @Nullable SessionId oldSessionId,
                           @Nonnull SessionLogEntryType entryType) {
        this.timestamp = Objects.requireNonNull(timestamp);
        this.sessionId = Objects.requireNonNull(sessionId);
        this.oldSessionId = oldSessionId;
        this.entryType = Objects.requireNonNull(entryType);
    }
}
