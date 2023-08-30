package com.example.secerrordemo.domain.security.log;

enum AuthenticationLogEntryType {
    LOGIN_SUCCESS,
    LOGOUT_SUCCESS,
    CREDENTIAL_CHANGE_SUCCESS,
    CREDENTIAL_CHANGE_FAILURE,
    LOGIN_FAILURE_BAD_CREDENTIALS,
    LOGIN_FAILURE_CREDENTIALS_EXPIRED,
    LOGIN_FAILURE_ACCOUNT_EXPIRED,
    LOGIN_FAILURE_ACCOUNT_LOCKED,
    LOGIN_FAILURE_ACCOUNT_DISABLED,
    LOGIN_FAILURE
}
