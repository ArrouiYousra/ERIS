CREATE TABLE banned_members (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    server_id BIGINT NOT NULL REFERENCES servers(id),
    banned_by_id BIGINT NOT NULL REFERENCES users(id),
    reason VARCHAR(500),
    banned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    UNIQUE(user_id, server_id)
);
