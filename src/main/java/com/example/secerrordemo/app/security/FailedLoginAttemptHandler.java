package com.example.secerrordemo.app.security;

import com.example.secerrordemo.domain.security.UserAccount;
import com.example.secerrordemo.domain.security.UserAccountRepository;
import com.example.secerrordemo.domain.security.Username;
import com.example.secerrordemo.infra.tx.TxManager;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
class FailedLoginAttemptHandler {

    private final TxManager txManager;
    private final UserAccountRepository userAccountRepository;

    FailedLoginAttemptHandler(TxManager txManager, UserAccountRepository userAccountRepository) {
        this.txManager = txManager;
        this.userAccountRepository = userAccountRepository;
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        txManager.runInNewTransaction(() -> userAccountRepository
                .findByUsername(Username.fromString(event.getAuthentication().getName()))
                .ifPresent(UserAccount::incrementFailedLoginAttempts));
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        txManager.runInNewTransaction(() -> userAccountRepository
                .findByUsername(Username.fromString(event.getAuthentication().getName()))
                .ifPresent(UserAccount::resetFailedLoginAttempts));
    }
}
