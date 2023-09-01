package com.example.secerrordemo.infra.security;

public final class Roles {

    private Roles() {
    }

    // Using a domain primitive or enum for roles would be better, but all annotations use strings, so we have to stick with that.

    public static final String ROLE_USER = "ROLE_USER";

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
}
