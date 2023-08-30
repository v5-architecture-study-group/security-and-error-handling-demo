package com.example.secerrordemo.testdata;

import com.example.secerrordemo.domain.security.UserAccount;
import com.example.secerrordemo.domain.security.UserAccountRepository;
import com.example.secerrordemo.domain.security.UserType;
import com.example.secerrordemo.domain.security.Username;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.temporal.ChronoUnit;

@Component
class TestUserGenerator {
    private static final Logger log = LoggerFactory.getLogger(TestUserGenerator.class);

    private static final Username ADMIN = Username.fromString("admin");
    private static final Username USER = Username.fromString("user");
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    public TestUserGenerator(UserAccountRepository userAccountRepository,
                             PasswordEncoder passwordEncoder,
                             Clock clock) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @PostConstruct
    public void generateData() {
        generateTestUser(UserType.USER, USER, "user_password");
        generateTestUser(UserType.ADMIN, ADMIN, "admin_password");
    }

    private void generateTestUser(UserType userType, Username username, String rawPassword) {
        var now = clock.instant();
        if (!userAccountRepository.existsByUsername(username)) {
            log.warn("Generating test user {}", username);
            userAccountRepository.save(new UserAccount(
                    userType,
                    username,
                    passwordEncoder.encode(rawPassword),
                    now,
                    now.plus(365, ChronoUnit.DAYS),
                    now.plus(7, ChronoUnit.DAYS)
            ));
        }
    }
}
