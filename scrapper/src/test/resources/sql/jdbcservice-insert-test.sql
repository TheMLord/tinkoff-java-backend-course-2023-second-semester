INSERT INTO tgchats (id, created_at)
VALUES (1, '2023-10-02T21:35:03Z'),
       (2, '2023-10-02T21:35:03Z'),
       (3, '2023-10-02T21:35:03Z');

INSERT INTO links (link_uri, content, created_at, last_modifying)
VALUES ('https://github.com/TheMLord/java-backend-course-2023-tinkoff1', '', '2023-10-02T21:35:03Z',
        '2024-03-15 13:49:14.240739 +00:00'),
       ('https://github.com/TheMLord/java-backend-course-2023-tinkoff2', '', '2023-10-02T21:35:03Z',
        '2024-03-14 13:49:14.240739 +00:00'),
       ('https://github.com/TheMLord/java-backend-course-2023-tinkoff3', '', '2023-10-02T21:35:03Z',
        '2024-03-14 13:49:14.240739 +00:00');

INSERT INTO subscriptions (chat_id, link_id, created_at)
VALUES (1, 1, '2023-10-02T21:35:03Z'),
       (1, 2, '2023-10-02T21:35:03Z'),
       (1, 3, '2023-10-02T21:35:03Z');
