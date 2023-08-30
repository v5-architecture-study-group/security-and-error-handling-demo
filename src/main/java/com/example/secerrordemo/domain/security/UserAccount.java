package com.example.secerrordemo.domain.security;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;


@Entity
public class UserAccount extends AbstractPersistable<Long> {

    public static final int MAX_FAILED_ATTEMPTS = 3;
    public static final Duration CREDENTIALS_VALID_FOR = Duration.ofDays(60);

    @Column(unique = true, nullable = false, length = Username.MAX_LENGTH)
    @Convert(converter = UsernameAttributeConverter.class)
    private Username username;

    @Column(nullable = false, length = ReadOnceEncodedPassword.MAX_LENGTH)
    private String encodedPassword;
    // Because of Hibernate, we can't use ReadOnceEncodedPassword with an AttributeConverter.
    // Within a single write-operation, Hibernate would read the value more than once, causing an error.
    private transient ReadOnceEncodedPassword readOnceEncodedPassword;

    @Column(nullable = false)
    private Instant accountNotValidBefore;

    @Column(nullable = false)
    private Instant accountNotValidAfter;

    @Column
    private Instant passwordNotValidAfter;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private int failedLoginAttempts;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    protected UserAccount() { // Required by Hibernate
    }

    public UserAccount(@Nonnull UserType userType,
                       @Nonnull Username username,
                       @Nonnull String encodedPassword,
                       @Nonnull Instant accountNotValidBefore,
                       @Nonnull Instant accountNotValidAfter,
                       @Nullable Instant passwordNotValidAfter) {
        this.userType = Objects.requireNonNull(userType, "userType must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.encodedPassword = Objects.requireNonNull(encodedPassword, "password must not be null");
        this.readOnceEncodedPassword = ReadOnceEncodedPassword.wrap(encodedPassword);
        this.accountNotValidBefore = Objects.requireNonNull(accountNotValidBefore, "accountNotValidBefore must not be null");
        this.accountNotValidAfter = Objects.requireNonNull(accountNotValidAfter, "accountNotValidAfter must not be null");
        this.passwordNotValidAfter = passwordNotValidAfter;
        this.enabled = true;
        this.failedLoginAttempts = 0;
    }

    @PostLoad
    private void postLoad() {
        this.readOnceEncodedPassword = ReadOnceEncodedPassword.wrap(encodedPassword);
    }

    public @Nonnull Username username() {
        return username;
    }

    public @Nonnull ReadOnceEncodedPassword password() {
        if (readOnceEncodedPassword == null) {
            readOnceEncodedPassword = ReadOnceEncodedPassword.empty();
        }
        return readOnceEncodedPassword;
    }

    public boolean isEnabled() {
        return enabled && encodedPassword != null;
    }

    public boolean isAccountValid(@Nonnull Clock clock) {
        var now = clock.instant();
        return !(now.isBefore(accountNotValidBefore) || now.isAfter(accountNotValidAfter));
    }

    public boolean isPasswordValid(@Nonnull Clock clock) {
        return passwordNotValidAfter == null || !clock.instant().isAfter(passwordNotValidAfter);
    }

    public @Nonnull Optional<Duration> passwordExpiresIn(@Nonnull Clock clock) {
        if (passwordNotValidAfter == null) {
            return Optional.empty();
        } else {
            return Optional.of(Duration.between(clock.instant(), passwordNotValidAfter));
        }
    }

    public void changePassword(@Nonnull ReadOnceRawPassword currentPassword, @Nonnull ReadOnceRawPassword newPassword,
                               @Nonnull PasswordEncoder passwordEncoder, @Nonnull Clock clock) {
        if (!isEnabled()) {
            throw new IllegalStateException("Account is disabled");
        }
        if (!isUnlocked()) {
            throw new IllegalStateException("Account is locked");
        }
        if (!isAccountValid(clock)) {
            throw new IllegalStateException("Account is expired or not yet valid");
        }
        if (!passwordEncoder.matches(password(), currentPassword)) {
            throw new IllegalArgumentException("Invalid current password");
        }

        this.encodedPassword = passwordEncoder.encode(newPassword).unwrap();
        this.readOnceEncodedPassword = ReadOnceEncodedPassword.wrap(encodedPassword);

        if (passwordNotValidAfter != null) {
            passwordNotValidAfter = clock.instant().plus(CREDENTIALS_VALID_FOR);
        }
    }

    public boolean isUnlocked() {
        return failedLoginAttempts < MAX_FAILED_ATTEMPTS;
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }

    public @Nonnull UserType userType() {
        return userType;
    }
}
