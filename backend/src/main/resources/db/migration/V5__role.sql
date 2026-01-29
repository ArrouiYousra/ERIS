CREATE TABLE IF NOT EXISTS roles (
    id           serial primary key,
    server_id    integer references servers(id),
    name         varchar(10)
);
