CREATE INDEX idx_items_name ON items (name);
CREATE INDEX idx_items_price ON items (price);

CREATE INDEX idx_quantity_promotions_item_id ON quantity_promotions (item_id);

CREATE INDEX idx_bundle_promotions_first_item_id ON bundle_promotions (first_bundle_item_id);
CREATE INDEX idx_bundle_promotions_item_pair ON bundle_promotions (first_bundle_item_id, second_bundle_item_id);
