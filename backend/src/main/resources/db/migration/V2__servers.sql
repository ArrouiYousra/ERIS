CREATE TABLE IF NOT EXISTS servers (
    id                  serial primary key,
    owner_id            integer references users(id),
    name                varchar(80)
);