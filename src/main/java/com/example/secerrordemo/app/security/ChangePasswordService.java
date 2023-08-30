package com.example.secerrordemo.app.security;

import com.example.secerrordemo.domain.security.ReadOnceRawPassword;
import jakarta.annotation.Nonnull;

public interface ChangePasswordService {

    void changePassword(@Nonnull ReadOnceRawPassword existingPassword,
                        @Nonnull ReadOnceRawPassword newPassword);
}
