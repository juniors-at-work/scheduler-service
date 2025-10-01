CREATE TABLE IF NOT EXISTS profiles
(
  user_id     UUID PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
  full_name   VARCHAR(255) NOT NULL CHECK (full_name <> ''),
  timezone    VARCHAR(255) NOT NULL DEFAULT 'UTC',
  description TEXT,
  is_active   BOOLEAN   NOT NULL DEFAULT TRUE,
  logo        TEXT,
  created_at  TIMESTAMP NOT NULL DEFAULT now(),
  updated_at  TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_profiles_active ON profiles(is_active);

INSERT INTO profiles (user_id, full_name, timezone, created_at, updated_at)
SELECT id, full_name, timezone, created_at, updated_at
FROM users;

ALTER TABLE users DROP COLUMN full_name, DROP COLUMN timezone;
