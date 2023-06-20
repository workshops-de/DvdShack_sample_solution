CREATE TABLE loyalty_card (
    id INTEGER PRIMARY KEY,
    points INT NOT NULL DEFAULT 0,
    customer_id INTEGER NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);
