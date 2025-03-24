CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    description VARCHAR(255),
    balance DOUBLE PRECISION NOT NULL,
    bank VARCHAR(50),
    account_type VARCHAR(20) NOT NULL,
    user_id UUID NOT NULL,
    closingDay INT,
    paymentDueDate INT,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE  CASCADE
);
