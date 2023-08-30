package com.example.secerrordemo.app.security;

import com.example.secerrordemo.domain.security.PasswordEncoder;
import com.example.secerrordemo.domain.security.ReadOnceEncodedPassword;
import com.example.secerrordemo.domain.security.ReadOnceRawPassword;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

@Component
class PasswordEncoderImpl implements PasswordEncoder {

    private final org.springframework.security.crypto.password.PasswordEncoder delegate;

    PasswordEncoderImpl(org.springframework.security.crypto.password.PasswordEncoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean matches(@Nonnull ReadOnceEncodedPassword encodedPassword, @Nonnull ReadOnceRawPassword rawPassword) {
        return rawPassword.map(rp -> delegate.matches(rp, encodedPassword.unwrap()));
    }

    @Nonnull
    @Override
    public ReadOnceEncodedPassword encode(@Nonnull ReadOnceRawPassword rawPassword) {
        return rawPassword.map(rp -> ReadOnceEncodedPassword.wrap(delegate.encode(rp)));
    }
}
