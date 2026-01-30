CREATE TABLE IF NOT EXISTS server_member (
    id                  serial primary key,
    nickname            varchar(255),
    server_id           integer references servers(id),
    role_id             integer references roles(id),
    user_id             integer references users(id),
    joined_at           timestamp DEFAULT CURRENT_TIMESTAMP

);