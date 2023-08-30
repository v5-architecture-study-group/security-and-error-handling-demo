package com.example.secerrordemo.app.security;

import com.example.secerrordemo.domain.security.PasswordEncoder;
import com.example.secerrordemo.domain.security.ReadOnceRawPassword;
import com.example.secerrordemo.domain.security.UserAccountRepository;
import com.example.secerrordemo.domain.security.log.AuthenticationLoggingService;
import com.example.secerrordemo.infra.tx.TxManager;
import jakarta.annotation.Nonnull;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
class ChangePasswordServiceImpl implements ChangePasswordService {

    private final TxManager txManager;
    private final CurrentUser currentUser;
    private final AuthenticationLoggingService authenticationLoggingService;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    ChangePasswordServiceImpl(TxManager txManager,
                              CurrentUser currentUser,
                              AuthenticationLoggingService authenticationLoggingService,
                              UserAccountRepository userAccountRepository,
                              PasswordEncoder passwordEncoder,
                              Clock clock) {
        this.txManager = txManager;
        this.currentUser = currentUser;
        this.authenticationLoggingService = authenticationLoggingService;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Override
    public void changePassword(@Nonnull ReadOnceRawPassword existingPassword,
                               @Nonnull ReadOnceRawPassword newPassword) {
        var username = currentUser.username();
        var sessionId = currentUser.sessionId();
        var ipAddress = currentUser.ipAddress();
        txManager.runInNewTransaction(() -> {
            var userAccount = userAccountRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            try {
                userAccount.changePassword(existingPassword, newPassword, passwordEncoder, clock);
            } catch (Throwable ex) {
                txManager.runInNewTransaction(() -> authenticationLoggingService.credentialChangeFailure(username, sessionId, ipAddress));
                throw ex;
            }
            authenticationLoggingService.credentialChangeSuccess(username, sessionId, ipAddress);
        });
    }
}
