-- Supprimer les anciennes colonnes
ALTER TABLE conversations DROP COLUMN sender_id;
ALTER TABLE conversations DROP COLUMN receiver_id;

-- Créer la table participants
CREATE TABLE conversation_participants (
    conversation_id bigint not null references conversations(id) on delete cascade,
    user_id integer not null references users(id) on delete cascade,
    PRIMARY KEY (conversation_id, user_id)
);