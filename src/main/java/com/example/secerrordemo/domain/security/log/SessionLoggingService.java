package com.example.secerrordemo.domain.security.log;

import com.example.secerrordemo.domain.security.SessionId;
import jakarta.annotation.Nonnull;

public interface SessionLoggingService {

    void sessionCreated(@Nonnull SessionId sessionId);

    void sessionDestroyed(@Nonnull SessionId sessionId);

    void sessionIdChanged(@Nonnull SessionId oldSessionId, @Nonnull SessionId newSessionId);
}
