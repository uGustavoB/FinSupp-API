ALTER TABLE transactions
    DROP COLUMN add_to_bill;

ALTER TABLE cards
    ALTER COLUMN card_limit DROP NOT NULL;