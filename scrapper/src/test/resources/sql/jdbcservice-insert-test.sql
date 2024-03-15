INSERT INTO tgchat (chat_id)
VALUES (1),
       (2),
       (3);

INSERT INTO link (link_name, content, last_modifying)
VALUES ('https://github.com/TheMLord/java-backend-course-2023-tinkoff1', '', '2024-03-15 13:49:14.240739 +00:00'),
       ('https://github.com/TheMLord/java-backend-course-2023-tinkoff2', '', '2024-03-14 13:49:14.240739 +00:00'),
       ('https://github.com/TheMLord/java-backend-course-2023-tinkoff3', '', '2024-03-14 13:49:14.240739 +00:00');

INSERT INTO relation (chat_id, link_id)
VALUES (1, 1),
       (1, 2),
       (1, 3);
