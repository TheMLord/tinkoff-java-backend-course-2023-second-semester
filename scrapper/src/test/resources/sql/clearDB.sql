TRUNCATE TABLE tgchats CASCADE;
TRUNCATE TABLE links CASCADE;
TRUNCATE TABLE subscriptions CASCADE;

ALTER SEQUENCE links_id_seq RESTART WITH 1;

