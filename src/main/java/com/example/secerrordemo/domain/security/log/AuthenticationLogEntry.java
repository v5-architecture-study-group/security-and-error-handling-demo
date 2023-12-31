package com.example.secerrordemo.domain.security.log;

import com.example.secerrordemo.domain.security.*;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "authentication_log")
class AuthenticationLogEntry extends AbstractPersistable<Long> {

    @Column(nullable = false, length = Username.MAX_LENGTH, name = "username")
    @Convert(converter = UsernameAttributeConverter.class)
    private Username username;
    @Column(nullable = false, name = "ts")
    private Instant timestamp;
    @Column(nullable = false, length = SessionId.MAX_LENGTH, name = "session_id")
    @Convert(converter = SessionIdAttributeConverter.class)
    private SessionId sessionId;
    @Column(nullable = false, length = IpAddress.Ipv6Address.MAX_LENGTH, name = "ip_address")
    @Convert(converter = IpAddressAttributeConverter.class)
    private IpAddress ipAddress;
    @Column(nullable = false, length = 100, name = "entry_type")
    @Enumerated(EnumType.STRING)
    private AuthenticationLogEntryType entryType;

    protected AuthenticationLogEntry() { // Required by Hibernate
    }

    public AuthenticationLogEntry(@Nonnull Username username,
                                  @Nonnull Instant timestamp,
                                  @Nonnull SessionId sessionId,
                                  @Nonnull IpAddress ipAddress,
                                  @Nonnull AuthenticationLogEntryType entryType) {
        this.username = Objects.requireNonNull(username);
        this.timestamp = Objects.requireNonNull(timestamp);
        this.sessionId = Objects.requireNonNull(sessionId);
        this.ipAddress = Objects.requireNonNull(ipAddress);
        this.entryType = Objects.requireNonNull(entryType);
    }
}
