INSERT INTO links (link_uri, created_at, content, last_modifying)
VALUES ('https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester',
        '2023-10-02T21:35:03Z',
        '{"githubRepositoryDTO":' ||
        '{"created_at":"2024-02-05T09:23:06Z",' ||
        '"updated_at":"2024-02-05T09:25:22Z",' ||
        '"pushed_at":"2024-02-22T10:12:19Z",' ||
        '"owner":{"login":"TheMLord","id":113773994}},' ||
        '"githubBranchesDTO":' ||
        '[{"name":"createdAccount"},' ||
        '{"name":"main"},' ||
        '{"name":"master"}' ||
        ']}',
        '2023-10-02T21:35:03Z');

INSERT INTO tgchats (id, created_at)
VALUES (1, '2023-10-02T21:35:03Z'),
       (2, '2023-10-02T21:35:03Z'),
       (3, '2023-10-02T21:35:03Z');

INSERT INTO subscriptions (chat_id, link_id, created_at)
VALUES (1, 1, '2023-10-02T21:35:03Z'),
       (3, 1, '2023-10-02T21:35:03Z');
