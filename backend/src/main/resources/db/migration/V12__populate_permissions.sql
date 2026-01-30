-- OWNER gets all permissions
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permission p
WHERE r.name = 'OWNER' AND r.server_id = 1;

-- ADMIN gets most
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permission p ON p.name IN (
    'MANAGE_ROLES',
    'KICK_MEMBER',
    'BAN_MEMBER',
    'DELETE_MESSAGE',
    'SEND_MESSAGE',
    'READ_CHANNEL'
)
WHERE r.name = 'ADMIN' AND r.server_id = 1;

-- MEMBER gets basic
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permission p ON p.name IN ('SEND_MESSAGE', 'READ_CHANNEL')
WHERE r.name = 'MEMBER' AND r.server_id = 1;