CREATE TABLE categories
(
    id          SERIAL PRIMARY KEY,
    description VARCHAR(20) NOT NULL UNIQUE
);