-- Supprimer les anciennes colonnes
ALTER TABLE conversations DROP COLUMN sender_id;
ALTER TABLE conversations DROP COLUMN receiver_id;

-- Créer la table participants
CREATE TABLE conversation_participants (
    conversation_id integer references conversations(id),
    user_id integer references users(id),
    PRIMARY KEY (conversation_id, user_id)
);