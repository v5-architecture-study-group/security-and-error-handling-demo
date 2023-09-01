package com.example.secerrordemo.app.security;

import com.example.secerrordemo.domain.security.UserAccount;
import com.example.secerrordemo.domain.security.UserAccountRepository;
import com.example.secerrordemo.domain.security.Username;
import com.example.secerrordemo.infra.tx.TxManager;
import jakarta.annotation.Nonnull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
class UserDetailsServiceImpl implements UserDetailsService {

    private final Clock clock;
    private final TxManager txManager;
    private final UserAccountRepository userAccountRepository;

    UserDetailsServiceImpl(Clock clock, TxManager txManager, UserAccountRepository userAccountRepository) {
        this.clock = clock;
        this.txManager = txManager;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var un = Username.fromString(username);
        if (!un.isValid()) {
            throw new UsernameNotFoundException("Invalid username");
        }
        return txManager.callInNewTransaction(() -> userAccountRepository.findByUsername(un))
                .map(this::createUser)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private @Nonnull User createUser(@Nonnull UserAccount userAccount) {
        return new User(userAccount.username().toString(),
                userAccount.password().unwrap(),
                userAccount.isEnabled(),
                userAccount.isAccountValid(clock),
                userAccount.isPasswordValid(clock),
                userAccount.isUnlocked(),
                userAccount.userType().roles().map(SimpleGrantedAuthority::new).toList());
    }
}
