INSERT INTO users (id, username, email, password_hash, role, created_at, updated_at)
VALUES ('d3e68c3b-2d6d-48a1-a037-99a390e9433e',
        'alice',
        'alice@mail.com',
        '{noop}12345',
        'USER',
        '2001-02-03T04:05:06.789012Z',
        '2001-02-03T04:05:06.789012Z'),
       ('9e7f7e33-4574-43b6-83d8-ded7f169c03f',
        'bob',
        'bob@mail.com',
        '{noop}54321',
        'USER',
        '2002-03-04T05:06:07.890123Z',
        '2002-03-04T05:06:07.890123Z');

INSERT INTO profiles (user_id, full_name, timezone, description, is_active, logo, created_at, updated_at)
VALUES ('d3e68c3b-2d6d-48a1-a037-99a390e9433e',
        'Alice Arno',
        'Europe/Paris',
        'Test description',
        true,
        'Logo',
        '2001-02-03T04:05:06.789012',
        '2001-02-03T04:05:06.789012');

INSERT INTO event_templates (id, user_id, title, description, duration_minutes, buffer_before_minutes,
                             buffer_after_minutes, is_group_event, max_participants, is_active, slug, start_date,
                             end_date, created_at, updated_at)
VALUES ('8840ddd5-e176-46d8-8f1b-babb00d989cd',
        'd3e68c3b-2d6d-48a1-a037-99a390e9433e',
        'Demo',
        'Sprint #42 demo',
        60,
        10,
        15,
        FALSE,
        1,
        TRUE,
        'b452644a-dba8-427a-8e44-d5c1bc528231',
        '2024-07-01T10:00:00.000000Z',
        '2024-07-04T17:00:00.000000',
        '2001-02-03T04:05:06.789012Z',
        '2001-03-04T05:06:07.890123Z');

