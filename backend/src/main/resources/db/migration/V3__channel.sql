CREATE TABLE IF NOT EXISTS channel (
    id                  serial primary key,
    server_id           integer references servers(id),
    user_id             integer references users(id),
    name                varchar(255),
    created_at          timestamp

);