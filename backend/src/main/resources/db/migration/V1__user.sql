CREATE TYPE statut AS ENUM ('online','offline','absent');
CREATE TYPE typing_statut AS ENUM ('is typing','is not typing');


CREATE TABLE IF NOT EXISTS users (
    id                  serial primary key,
    username            varchar(255),
    email               varchar(255),
    password            varchar(255),
    display_name        varchar(255),
    current_statut      statut,
    current_typing_statut  typing_statut,
    created_at          timestamp,
    updated_at          timestamp
);