CREATE TABLE IF NOT EXISTS conversations (
    id                  bigserial primary key,
    sender_id           integer references users(id),
	receiver_id         integer references users(id),
    created_at          timestamp DEFAULT CURRENT_TIMESTAMP
);