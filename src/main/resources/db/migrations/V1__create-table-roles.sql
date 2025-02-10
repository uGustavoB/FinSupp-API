create table roles (
    role    varchar(50) not null,
    user_id uuid        not null
        constraint fk_user
            references users
            on delete cascade
);