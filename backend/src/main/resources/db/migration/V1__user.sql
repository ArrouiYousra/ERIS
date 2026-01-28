CREATE TYPE statut AS ENUM ('online','offline','absent');

CREATE TABLE IF NOT EXISTS users (
    id                  serial primary key,
    username            varchar(255),
    email               varchar(255),
    password            varchar(255),
    display_name        varchar(255),
    current_statut      statut,
    created_at          timestamp,
    updated_at          timestamp
);