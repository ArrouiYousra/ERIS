CREATE TABLE IF NOT EXISTS private_messages (
    id              bigserial primary key,
    conversation_id bigint not null references conversations(id) on delete cascade,
    sender_id       bigint not null references users(id) on delete cascade,
    content         text not null,
    created_at      timestamp DEFAULT CURRENT_TIMESTAMP,
    updated_at      timestamp DEFAULT CURRENT_TIMESTAMP
);