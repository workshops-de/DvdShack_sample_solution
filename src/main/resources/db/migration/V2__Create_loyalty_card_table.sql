CREATE TABLE loyalty_card (
    id BIGINT PRIMARY KEY,
    points INT NOT NULL DEFAULT 0,
    customer_id BIGINT,
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);
