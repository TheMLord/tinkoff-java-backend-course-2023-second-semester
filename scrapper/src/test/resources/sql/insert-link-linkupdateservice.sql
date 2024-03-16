INSERT INTO link (link_name, content)
VALUES ('https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester',
        '{"created_at":"2023-10-02T21:35:03Z","updated_at":"2023-10-18T13:48:21Z","pushed_at":"2023-12-17T18:16:59Z","owner":{"login":"TheMLord","id":113773994}}'),
       ('https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester2',
        '{"created_at":"2023-10-02T21:35:03Z","updated_at":"2023-10-18T13:48:21Z","pushed_at":"2023-10-17T18:16:59Z","owner":{"login":"TheMLord","id":113773994}}');

INSERT INTO tgchat (chat_id)
VALUES (1),
       (2),
       (3);

INSERT INTO relation (chat_id, link_id)
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (3, 1);
