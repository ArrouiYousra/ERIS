INSERT INTO conversations (sender_id, receiver_id, created_at) VALUES
(1, 2, CURRENT_TIMESTAMP - INTERVAL '2 days'),
(2, 3, CURRENT_TIMESTAMP - INTERVAL '1 day'),
(3, 1, CURRENT_TIMESTAMP);