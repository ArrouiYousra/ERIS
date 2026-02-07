CREATE TABLE IF NOS EXISTS invitation (
    id           serial primary key,
    code         varchar(20),
    server_id    integer references servers(id),
    created_at      timestamp,
    expires_at  timestamp,


);

)