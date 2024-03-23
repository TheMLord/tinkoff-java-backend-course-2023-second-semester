INSERT INTO links (link_uri, created_at, content)
VALUES ('https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester',
        '2023-10-02T21:35:03Z',
        '{"githubRepositoryDTO":{"created_at":"2024-02-05T09:23:06Z","updated_at":"2024-02-05T09:25:22Z","pushed_at":"2024-02-22T10:12:19Z","owner":{"login":"TheMLord","id":113773994}},"githubBranchesDTO":[{"name":"develop"},{"name":"hw2"},{"name":"hw3"},{"name":"hw4"},{"name":"hw5_bonus"},{"name":"hw5"},{"name":"main"},{"name":"master"}]}'),
       ('https://github.com/TheMLord/tinkoff-java-backend-course-2023-second-semester2',
        '2023-10-02T21:35:03Z',
        '{"githubRepositoryDTO":{"created_at":"2024-02-05T09:23:06Z","updated_at":"2024-02-05T09:25:22Z","pushed_at":"2024-02-22T10:12:19Z","owner":{"login":"TheMLord","id":113773994}},"githubBranchesDTO":[{"name":"develop"},{"name":"hw2"},{"name":"hw3"},{"name":"hw4"},{"name":"hw5_bonus"},{"name":"hw5"},{"name":"main"},{"name":"master"}]}');

INSERT INTO tgchats (id, created_at)
VALUES (1, '2023-10-02T21:35:03Z'),
       (2, '2023-10-02T21:35:03Z'),
       (3, '2023-10-02T21:35:03Z');

INSERT INTO subscriptions (chat_id, link_id, created_at)
VALUES (1, 1, '2023-10-02T21:35:03Z'),
       (1, 2, '2023-10-02T21:35:03Z'),
       (2, 2, '2023-10-02T21:35:03Z'),
       (3, 1, '2023-10-02T21:35:03Z');
