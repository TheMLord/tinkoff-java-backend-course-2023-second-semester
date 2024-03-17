CREATE TABLE tgChat
(
    chat_id    BIGINT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()      NOT NULL,
    created_by TEXT                     DEFAULT 'themlord' NOT NULL,

    PRIMARY KEY (chat_id)
)
