CREATE TABLE user_account
(
    id                       BIGINT       NOT NULL,
    username                 VARCHAR(100) NOT NULL UNIQUE,
    encoded_password         VARCHAR(100) NOT NULL,
    not_valid_before         TIMESTAMP    NOT NULL,
    not_valid_after          TIMESTAMP    NOT NULL,
    password_not_valid_after TIMESTAMP,
    enabled                  BOOL         NOT NULL,
    failed_login_attempts    INT          NOT NULL,
    user_type                VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE SEQUENCE user_account_seq INCREMENT 50;

CREATE TABLE authentication_log
(
    id         BIGINT       NOT NULL,
    username   VARCHAR(100) NOT NULL,
    ts         TIMESTAMP    NOT NULL,
    session_id VARCHAR(100) NOT NULL,
    ip_address VARCHAR(39)  NOT NULL,
    entry_type VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE SEQUENCE authentication_log_seq INCREMENT 50;

CREATE TABLE session_log
(
    id             BIGINT       NOT NULL,
    ts             TIMESTAMP    NOT NULL,
    session_id     VARCHAR(100) NOT NULL,
    old_session_id VARCHAR(100),
    entry_type     VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

CREATE SEQUENCE session_log_seq INCREMENT 50;
