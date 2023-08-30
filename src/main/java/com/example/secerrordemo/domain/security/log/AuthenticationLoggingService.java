package com.example.secerrordemo.domain.security.log;

import com.example.secerrordemo.domain.security.IpAddress;
import com.example.secerrordemo.domain.security.SessionId;
import com.example.secerrordemo.domain.security.Username;
import jakarta.annotation.Nonnull;

public interface AuthenticationLoggingService {
    void authenticationSuccess(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress);

    void logoutSuccess(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress);

    void badCredentials(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress);

    void credentialsExpired(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress);

    void accountExpired(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress);

    void accountLocked(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress);

    void accountDisabled(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress);

    void otherAuthenticationFailure(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress);

    void credentialChangeSuccess(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress);

    void credentialChangeFailure(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress);
}
