CREATE TABLE users
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    username       VARCHAR(100) NOT NULL UNIQUE,
    email          VARCHAR(255) NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL,
    email_verified BOOLEAN      NOT NULL DEFAULT FALSE,

    created_at     TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at     TIMESTAMP NULL
);


