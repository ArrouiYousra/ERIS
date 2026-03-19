CREATE TABLE IF NOT EXISTS private_messages (
    id                  bigserial primary key,
	conversation_id     integer references conversations(id),
    sender_id           integer references users(id),
	content				text,
    created_at          timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at          timestamp DEFAULT CURRENT_TIMESTAMP
);