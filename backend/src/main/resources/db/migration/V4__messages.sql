CREATE TABLE IF NOT EXISTS messages (
    id                  bigserial primary key,
    sender_id           integer references users(id),
    channel_id          integer references channel(id),
    content             text,
    created_at          timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at          timestamp DEFAULT CURRENT_TIMESTAMP

);