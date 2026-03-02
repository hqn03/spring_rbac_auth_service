CREATE TABLE email_verification_tokens
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    token      VARCHAR(255) NOT NULL UNIQUE,
    used       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at TIMESTAMP    NOT NULL,


    CONSTRAINT fk_email_verification_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE,

    CONSTRAINT uk_email_verification_token
        UNIQUE (token)
);

CREATE INDEX idx_email_verification_user
    ON email_verification_tokens(user_id);

CREATE INDEX idx_email_verification_expiry
    ON email_verification_tokens(expired_at);



