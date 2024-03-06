CREATE TABLE relation
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY,
    chat_id    BIGINT,
    link_id    BIGINT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()      NOT NULL,
    created_by TEXT                     DEFAULT 'themlord' NOT NULL,

    PRIMARY KEY (id),
    CONSTRAINT fk_chat FOREIGN KEY (chat_id) REFERENCES tgChat (chat_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_link FOREIGN KEY (link_id) REFERENCES link (id) ON DELETE CASCADE ON UPDATE CASCADE
)
