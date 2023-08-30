package com.example.secerrordemo.domain.security.log;

import com.example.secerrordemo.domain.security.SessionId;
import com.example.secerrordemo.domain.security.SessionIdAttributeConverter;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.Instant;
import java.util.Objects;

@Entity
class SessionLogEntry extends AbstractPersistable<Long> {

    @Column(nullable = false)
    private Instant timestamp;
    @Column(nullable = false, length = SessionId.MAX_LENGTH)
    @Convert(converter = SessionIdAttributeConverter.class)
    private SessionId sessionId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SessionLogEntryType entryType;

    protected SessionLogEntry() { // Required by Hibernate
    }

    public SessionLogEntry(@Nonnull Instant timestamp, @Nonnull SessionId sessionId,
                           @Nonnull SessionLogEntryType entryType) {
        this.timestamp = Objects.requireNonNull(timestamp);
        this.sessionId = Objects.requireNonNull(sessionId);
        this.entryType = Objects.requireNonNull(entryType);
    }
}
