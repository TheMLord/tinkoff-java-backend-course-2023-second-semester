CREATE TABLE links
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    link_uri   TEXT                                NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE            NOT NULL,
    created_by TEXT DEFAULT 'themlord'             NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (link_uri)
)
