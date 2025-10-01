ALTER TABLE users RENAME COLUMN user_login TO username;

CREATE UNIQUE INDEX username_unique_idx ON users (UPPER(username));
