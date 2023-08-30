package com.example.secerrordemo.domain.security;

import jakarta.annotation.Nonnull;

public interface PasswordEncoder {
    boolean matches(@Nonnull ReadOnceEncodedPassword encodedPassword, @Nonnull ReadOnceRawPassword rawPassword);

    @Nonnull
    ReadOnceEncodedPassword encode(@Nonnull ReadOnceRawPassword rawPassword);
}
