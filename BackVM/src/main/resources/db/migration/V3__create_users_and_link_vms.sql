CREATE TABLE users (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(200) NOT NULL,
    plan        VARCHAR(20)  NOT NULL DEFAULT 'BASIC',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

DELETE FROM virtual_machines;

ALTER TABLE virtual_machines
    ADD COLUMN user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE;

CREATE INDEX idx_virtual_machines_user_id ON virtual_machines(user_id);
