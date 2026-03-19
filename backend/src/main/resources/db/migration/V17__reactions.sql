CREATE TABLE IF NOT EXISTS reactions (
    user_id bigserial NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message_id bigserial NOT NULL REFERENCES messages(id) ON DELETE CASCADE,
    emoji VARCHAR(10) NOT NULL CHECK (
        char_length(emoji) BETWEEN 1 AND 2
    ),
    PRIMARY KEY (user_id, message_id)
);