package com.example.secerrordemo.domain.security.log;

import com.example.secerrordemo.domain.security.SessionId;
import jakarta.annotation.Nonnull;

public interface SessionLoggingService {

    void sessionCreated(@Nonnull SessionId sessionId);

    void sessionDestroyed(@Nonnull SessionId sessionId);

    void sessionDeleted(@Nonnull SessionId sessionId);

    void sessionExpired(@Nonnull SessionId sessionId);
}
