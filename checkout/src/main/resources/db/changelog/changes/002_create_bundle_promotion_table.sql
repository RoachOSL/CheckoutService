CREATE TABLE IF NOT EXISTS bundle_promotions (
    id BIGSERIAL PRIMARY KEY,
    first_bundle_item_id BIGINT NOT NULL,
    first_item_required_quantity INT NOT NULL,
    second_bundle_item_id BIGINT NOT NULL,
    second_item_required_quantity INT NOT NULL,
    bundle_price DECIMAL(20,2) DEFAULT 0,
    FOREIGN KEY (first_bundle_item_id) REFERENCES items(id),
    FOREIGN KEY (second_bundle_item_id) REFERENCES items(id)
);