CREATE TABLE transactions
(
    id               SERIAL PRIMARY KEY,
    account_id       UUID           NOT NULL,
    recipient_id     UUID,
    category_id      INT            NOT NULL,
    transaction_type VARCHAR(20)    NOT NULL,
    amount           DECIMAL(15, 2) NOT NULL CHECK (amount > 0),
    transaction_date DATE           NOT NULL,
    description      VARCHAR(255),

    CONSTRAINT fk_account FOREIGN KEY (account_id)
        REFERENCES accounts (id) ON DELETE CASCADE,

    CONSTRAINT fk_recipient_account FOREIGN KEY (recipient_id)
        REFERENCES accounts (id) ON DELETE SET NULL,

    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id)
        REFERENCES categories (id) ON DELETE CASCADE
);
