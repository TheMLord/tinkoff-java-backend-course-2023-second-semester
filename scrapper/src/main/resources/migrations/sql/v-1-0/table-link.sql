CREATE TABLE link
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY,
    link_name  TEXT                                        NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()      NOT NULL,
    created_by TEXT                     DEFAULT 'themlord' NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (link_name)
)
