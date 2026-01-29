INSERT INTO users (
    username,
    email,
    password,
    display_name,
    current_statut,
    current_typing_statut,
    created_at,
    updated_at
) VALUES
(
    'alex',
    'alex@test.com',
    '$2a$10$fakehashedpasswordalex',
    'Alex',
    'online',
    'is not typing',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'marie',
    'marie@test.com',
    '$2a$10$fakehashedpasswordmarie',
    'Marie',
    'offline',
    'is not typing',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'thomas',
    'thomas@test.com',
    '$2a$10$fakehashedpasswordthomas',
    'Thomas',
    'absent',
    'is typing',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'lucie',
    'lucie@test.com',
    '$2a$10$fakehashedpasswordlucie',
    'Lucie',
    'online',
    'is typing',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
),
(
    'kevin',
    'kevin@test.com',
    '$2a$10$fakehashedpasswordkevin',
    'Kevin',
    'offline',
    'is not typing',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
