CREATE TABLE IF NOT EXISTS quantity_promotions (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    required_quantity INT NOT NULL,
    quantity_promotion_price DECIMAL(20,2) DEFAULT 0,
    FOREIGN KEY (item_id) REFERENCES items(id)
);
