package com.example.secerrordemo.domain.security;

import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.stream.Stream;

public enum UserType {
    USER(Roles.ROLE_USER),
    ADMIN(Roles.ROLE_USER, Roles.ROLE_ADMIN);

    private final List<String> roles;

    UserType(String... roles) {
        this.roles = List.of(roles);
    }

    public @Nonnull Stream<String> roles() {
        return roles.stream();
    }
}
