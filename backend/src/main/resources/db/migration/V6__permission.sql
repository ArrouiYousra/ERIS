CREATE TABLE IF NOT EXISTS permission (
    id           serial primary key,
    name         varchar(20)
);

CREATE TABLE IF NOT EXISTS role_permission (
    permission_id           integer references permission(id),
    role_id                 integer references roles(id)
);

INSERT INTO permission (name) VALUES
('MANAGE_SERVER'),
('MANAGE_ROLES'),
('KICK_MEMBER'),
('BAN_MEMBER'),
('DELETE_MESSAGE'),
('SEND_MESSAGE'),
('READ_CHANNEL');