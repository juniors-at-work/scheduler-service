-- 1. Проверка на создание таблиц
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- 2. Проверка колонки в ключевых таблицах (например, users и event_templates)
-- 2.1
SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'users';
-- 2.2
SELECT column_name, data_type
FROM information_schema.columns
WHERE table_name = 'event_templates';

--3. Проверка на добавляем пользователя
INSERT INTO users (email, password_hash, full_name, timezone)
VALUES (
    'test@example.com',
    'hash123',
    'Тестовый Пользователь',
    'Europe/Moscow'
);

-- 4. Добавление шаблона события
INSERT INTO event_templates (
    user_id, title, description, duration_minutes,
    buffer_before_minutes, buffer_after_minutes,
    is_group_event, slug, timezone
)
VALUES (
    (SELECT id FROM users WHERE email = 'test@example.com'),
    'Тестовая консультация',
    'Описание тестового события',
    30, 5, 10, FALSE, 'test-consult', 'Europe/Moscow'
);
-- 4.1. Проверка на добавление шаблона события
SELECT *
FROM event_templates
WHERE slug = 'test-consult'
  AND user_id = (SELECT id FROM users WHERE email = 'test@example.com');

-- 5. Добавление доступности пользователя
INSERT INTO availability_rules (user_id, weekday, start_time, end_time)
VALUES (
    (SELECT id FROM users WHERE email = 'test@example.com'),
    1,
    '09:00:00',
    '18:00:00'
);
-- 5.1 Проверка на добавление доступности пользователя
SELECT * FROM users;
SELECT * FROM event_templates;

-- 6. Проверка связи event_templates - users (1 запись должна быть)
SELECT et.title, u.email
FROM event_templates et
JOIN users u ON et.user_id = u.id;
-- 6.1 Создание временного слота
INSERT INTO time_slots (event_template_id, start_time, end_time)
VALUES (
    (SELECT id FROM event_templates WHERE slug = 'test-consult'),
    '2023-11-20 10:00:00',
    '2023-11-20 10:30:00'
);
-- 6.2 Создание бронирования
INSERT INTO bookings (
    event_template_id, slot_id, invitee_name, invitee_email, start_time, end_time
)
VALUES (
    (SELECT id FROM event_templates WHERE slug = 'test-consult'),
    (SELECT id FROM time_slots WHERE event_template_id = (SELECT id FROM event_templates WHERE slug = 'test-consult') ORDER BY start_time LIMIT 1),
    'Иван Тестовый',
    'ivan@test.com',
    '2023-11-20 10:00:00',
    '2023-11-20 10:30:00'
);
-- 6.3 Проверка итоговая
SELECT b.id, et.title, b.invitee_email, ts.start_time
FROM bookings b
JOIN event_templates et ON b.event_template_id = et.id
LEFT JOIN time_slots ts ON b.slot_id = ts.id;

-- 7. Получение всех активных шаблонов событий пользователя
SELECT id, title, description, is_active
FROM event_templates
WHERE user_id = (SELECT id FROM users WHERE email = 'test@example.com')
  AND is_active = TRUE;

-- 8. Получение всех доступных (is_available = TRUE) временных слотов для конкретного шаблона события
SELECT id, start_time, end_time
FROM time_slots
WHERE event_template_id = (SELECT id FROM event_templates WHERE slug = 'test-consult')
  AND is_available = TRUE
ORDER BY start_time;

SELECT * FROM availability_rules
WHERE user_id = (SELECT id FROM users WHERE email = 'test@example.com');

--9. Получение расписания доступности пользователя на конкретный день недели (понедельник)
SELECT weekday, start_time, end_time
FROM availability_rules
WHERE user_id = (SELECT id FROM users WHERE email = 'test@example.com')
  AND weekday = 1;

--10. Проверка количества бронирований для каждого шаблона события
SELECT et.title, COUNT(b.id) AS bookings_count
FROM event_templates et
LEFT JOIN bookings b ON et.id = b.event_template_id AND b.is_canceled = FALSE
GROUP BY et.title
ORDER BY bookings_count DESC;

-- 11. Получение пользователей с количеством их активных шаблонов событий
SELECT u.email, COUNT(et.id) AS active_event_templates
FROM users u
LEFT JOIN event_templates et ON u.id = et.user_id AND et.is_active = TRUE
GROUP BY u.email
ORDER BY active_event_templates DESC;