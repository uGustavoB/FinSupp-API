create table users (
    id       uuid default gen_random_uuid() not null
        primary key,
    name     varchar(255)                   not null,
    email    varchar(255)                   not null
        unique,
    password varchar(255)                   not null
);