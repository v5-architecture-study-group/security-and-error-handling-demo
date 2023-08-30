package com.example.secerrordemo.domain.security.log;

import com.example.secerrordemo.domain.security.IpAddress;
import com.example.secerrordemo.domain.security.SessionId;
import com.example.secerrordemo.domain.security.Username;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
class AuthenticationLoggingServiceImpl implements AuthenticationLoggingService {

    private final Clock clock;
    private final AuthenticationLogEntryRepository repository;

    AuthenticationLoggingServiceImpl(Clock clock,
                                     AuthenticationLogEntryRepository repository) {
        this.clock = clock;
        this.repository = repository;
    }

    @Override
    public void authenticationSuccess(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress) {
        repository.save(new AuthenticationLogEntry(username, clock.instant(), session, ipAddress, AuthenticationLogEntryType.LOGIN_SUCCESS));
    }

    @Override
    public void logoutSuccess(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress) {
        repository.save(new AuthenticationLogEntry(username, clock.instant(), session, ipAddress, AuthenticationLogEntryType.LOGOUT_SUCCESS));
    }

    @Override
    public void badCredentials(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress) {
        repository.save(new AuthenticationLogEntry(username, clock.instant(), session, ipAddress, AuthenticationLogEntryType.LOGIN_FAILURE_BAD_CREDENTIALS));
    }

    @Override
    public void credentialsExpired(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress) {
        repository.save(new AuthenticationLogEntry(username, clock.instant(), session, ipAddress, AuthenticationLogEntryType.LOGIN_FAILURE_CREDENTIALS_EXPIRED));
    }

    @Override
    public void accountExpired(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress) {
        repository.save(new AuthenticationLogEntry(username, clock.instant(), session, ipAddress, AuthenticationLogEntryType.LOGIN_FAILURE_ACCOUNT_EXPIRED));
    }

    @Override
    public void accountLocked(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress) {
        repository.save(new AuthenticationLogEntry(username, clock.instant(), session, ipAddress, AuthenticationLogEntryType.LOGIN_FAILURE_ACCOUNT_LOCKED));
    }

    @Override
    public void accountDisabled(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress) {
        repository.save(new AuthenticationLogEntry(username, clock.instant(), session, ipAddress, AuthenticationLogEntryType.LOGIN_FAILURE_ACCOUNT_DISABLED));
    }

    @Override
    public void otherAuthenticationFailure(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress) {
        repository.save(new AuthenticationLogEntry(username, clock.instant(), session, ipAddress, AuthenticationLogEntryType.LOGIN_FAILURE));
    }

    @Override
    public void credentialChangeSuccess(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress) {
        repository.save(new AuthenticationLogEntry(username, clock.instant(), session, ipAddress, AuthenticationLogEntryType.CREDENTIAL_CHANGE_SUCCESS));
    }

    @Override
    public void credentialChangeFailure(@Nonnull Username username, @Nonnull SessionId session, @Nonnull IpAddress ipAddress) {
        repository.save(new AuthenticationLogEntry(username, clock.instant(), session, ipAddress, AuthenticationLogEntryType.CREDENTIAL_CHANGE_FAILURE));
    }
}
