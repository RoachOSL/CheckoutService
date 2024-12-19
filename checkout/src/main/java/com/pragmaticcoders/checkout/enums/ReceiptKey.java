package com.pragmaticcoders.checkout.enums;

import lombok.Getter;

@Getter
public enum ReceiptKey {

    ITEM_NAME("Item Name"),
    QUANTITY("Quantity"),
    TOTAL_COST("Total Cost"),
    ITEMS("Items"),
    TOTAL_DISCOUNT("Total Discount");

    private final String key;

    ReceiptKey(String key) {
        this.key = key;
    }
}
