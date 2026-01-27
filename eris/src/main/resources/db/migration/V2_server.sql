CREATE TABLE IF NOT EXISTS servers (
    id                  serial primary key,
    owner_id            integer references user(id)
    channel_id          integer references channel(id)
    name                varchar(80)

);