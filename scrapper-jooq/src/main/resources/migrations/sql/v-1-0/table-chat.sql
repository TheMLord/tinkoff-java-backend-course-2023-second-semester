CREATE TABLE tgchats
(
    id         BIGINT                   NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by TEXT DEFAULT 'themlord'  NOT NULL,

    PRIMARY KEY (id)
)
