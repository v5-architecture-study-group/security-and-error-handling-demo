package com.example.secerrordemo.domain.security;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    boolean existsByUsername(@Nonnull Username username);

    @Nonnull
    Optional<UserAccount> findByUsername(@Nonnull Username username);
}
