CREATE TABLE IF NOT EXISTS tokens
(
    user_id    UUID          NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token      VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS user_id_unique_idx ON tokens (user_id);