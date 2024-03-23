CREATE TABLE subscriptions
(
    chat_id    BIGINT,
    link_id    BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by TEXT DEFAULT 'themlord'  NOT NULL,

    PRIMARY KEY (chat_id, link_id),
    CONSTRAINT fk_chats FOREIGN KEY (chat_id) REFERENCES tgchats (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_links FOREIGN KEY (link_id) REFERENCES links (id) ON DELETE CASCADE ON UPDATE CASCADE
)
