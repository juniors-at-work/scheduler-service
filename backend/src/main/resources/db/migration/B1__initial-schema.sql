     CREATE TYPE booking_status AS ENUM (
                   'PENDING',
                   'CONFIRMED',
                   'CANCELED'
     );

     CREATE TABLE IF NOT EXISTS users (
     id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     email                    VARCHAR(255) NOT NULL CHECK (email <> ''),
     password_hash            VARCHAR(255) NOT NULL CHECK (password_hash <> ''),
     full_name                VARCHAR(255) NOT NULL CHECK (full_name <> ''),
     timezone                 VARCHAR(255) NOT NULL DEFAULT 'UTC',
     created_at               TIMESTAMP NOT NULL DEFAULT now(),
     updated_at               TIMESTAMP NOT NULL DEFAULT now()
     );

     CREATE UNIQUE INDEX IF NOT EXISTS email_unique_idx ON users (UPPER(email));

     CREATE TABLE IF NOT EXISTS event_templates (
     id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     user_id                  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
     title                    VARCHAR(255) NOT NULL CHECK (title <> ''),
     description              VARCHAR(512) NOT NULL CHECK (description <> ''),
     duration_minutes         INT NOT NULL CHECK (duration_minutes > 0),
     buffer_before_minutes    INT DEFAULT 0 CHECK (buffer_before_minutes >= 0),
     buffer_after_minutes     INT DEFAULT 0 CHECK (buffer_after_minutes >= 0),
     is_group_event           BOOLEAN NOT NULL DEFAULT FALSE,
     max_participants         INT DEFAULT 0 CHECK (max_participants >= 0),
     is_active                BOOLEAN NOT NULL DEFAULT TRUE,
     slug                     TEXT UNIQUE NOT NULL,
     start_date               TIMESTAMP NOT NULL DEFAULT now(),
     end_date                 TIMESTAMP NULL,
     created_at               TIMESTAMP NOT NULL DEFAULT now(),
     updated_at               TIMESTAMP NOT NULL DEFAULT now(),
     CHECK (end_date - start_date >= INTERVAL '15 minutes')
     );

     CREATE TABLE IF NOT EXISTS availability_rules (
     id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     user_id                  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
     weekday                  INT NOT NULL CHECK (weekday BETWEEN 1 AND 7),
     start_time               TIME NOT NULL,
     end_time                 TIME NOT NULL,
     created_at               TIMESTAMP NOT NULL DEFAULT now(),
     updated_at               TIMESTAMP NOT NULL DEFAULT now(),
     CHECK (end_time - start_time >= INTERVAL '15 minutes')
     );

     CREATE TABLE IF NOT EXISTS time_slots (
     id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     event_template_id        UUID NOT NULL REFERENCES event_templates(id) ON DELETE CASCADE,
     start_time               TIMESTAMP NOT NULL,
     end_time                 TIMESTAMP NOT NULL,
     is_available             BOOLEAN NOT NULL DEFAULT TRUE,
     created_at               TIMESTAMP NOT NULL DEFAULT now(),
     updated_at               TIMESTAMP NOT NULL DEFAULT now(),
     UNIQUE(event_template_id, start_time),
     CHECK (end_time - start_time >= INTERVAL '15 minutes')
     );

     -- Индекс для быстрого поиска доступных слотов по event_template_id и start_time
     CREATE INDEX IF NOT EXISTS idx_time_slots_available ON time_slots(event_template_id, start_time)
     WHERE is_available = TRUE;

     -- Индекс для ускорения поиска по интервалам времени (start_time, end_time) в рамках event_template_id
     CREATE INDEX IF NOT EXISTS idx_time_slots_timerange ON time_slots(event_template_id, start_time, end_time);

     CREATE TABLE IF NOT EXISTS bookings (
     id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     event_template_id        UUID NOT NULL REFERENCES event_templates(id) ON DELETE CASCADE,
     slot_id                  UUID REFERENCES time_slots(id) ON DELETE SET NULL,
     invitee_name             VARCHAR(100) NOT NULL CHECK (invitee_name <> ''),
     invitee_email            VARCHAR(255) NOT NULL CHECK (invitee_email <> ''),
     start_time               TIMESTAMP NOT NULL,
     end_time                 TIMESTAMP NOT NULL,
     is_canceled              BOOLEAN NOT NULL DEFAULT FALSE,
     created_at               TIMESTAMP NOT NULL DEFAULT now(),
     updated_at               TIMESTAMP NOT NULL DEFAULT now(),
     CHECK (end_time - start_time >= INTERVAL '15 minutes')
     );

     CREATE TABLE IF NOT EXISTS booking_participants (
     id                       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     booking_id               UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
     email                    VARCHAR(255) NOT NULL CHECK (email <> ''),
     name                     VARCHAR(255) NOT NULL CHECK (name <> ''),
     status                   booking_status DEFAULT 'PENDING', -- 'CONFIRMED', 'CANCELED'
     created_at               TIMESTAMP NOT NULL DEFAULT now(),
     updated_at               TIMESTAMP NOT NULL DEFAULT now()
     );

