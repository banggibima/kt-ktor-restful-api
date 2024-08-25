CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    username   TEXT      NOT NULL,
    password   TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);