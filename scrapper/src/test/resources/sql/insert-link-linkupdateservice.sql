INSERT INTO links (link_uri, created_at, last_modifying, content)
VALUES ('https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester',
        '2023-10-02T21:35:03Z',
        '2023-10-02T21:35:03Z',
        '{"created_at":"2023-10-02T21:35:03Z","updated_at":"2023-10-18T13:48:21Z","pushed_at":"2023-12-17T18:16:59Z","owner":{"login":"TheMLord","id":113773994}}'),
       ('https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester2',
        '2023-10-02T21:35:03Z',
        '2023-10-02T21:35:03Z',
        '{"created_at":"2023-10-02T21:35:03Z","updated_at":"2023-10-18T13:48:21Z","pushed_at":"2023-10-17T18:16:59Z","owner":{"login":"TheMLord","id":113773994}}');

INSERT INTO tgchats (id, created_at)
VALUES (1, '2023-10-02T21:35:03Z'),
       (2, '2023-10-02T21:35:03Z'),
       (3, '2023-10-02T21:35:03Z');

INSERT INTO subscriptions (chat_id, link_id, created_at)
VALUES (1, 1, '2023-10-02T21:35:03Z'),
       (1, 2, '2023-10-02T21:35:03Z'),
       (2, 2, '2023-10-02T21:35:03Z'),
       (3, 1, '2023-10-02T21:35:03Z');
