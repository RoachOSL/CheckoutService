package com.pragmaticcoders.checkout.components;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Component
@SessionScope
public class SessionCart {

    private final Map<Long, Integer> scannedItems = new HashMap<>();
    private final Map<Long, BigDecimal> discounts = new HashMap<>();
    @Getter
    private final Map<Long, Integer> bundleItems = new HashMap<>();

    public void addItem(Long itemId, int quantity) {
        scannedItems.merge(itemId, quantity, Integer::sum);
    }

    public void addDiscount(Long itemId, BigDecimal discount) {
        discounts.merge(itemId, discount, BigDecimal::add);
    }

    public BigDecimal getDiscount(Long itemId) {
        return discounts.getOrDefault(itemId, BigDecimal.ZERO);
    }

    public void addBundleItem(Long itemId, int quantity, BigDecimal price) {
        bundleItems.put(itemId, bundleItems.getOrDefault(itemId, 0) + quantity);
    }

    public void clear() {
        scannedItems.clear();
        discounts.clear();
        bundleItems.clear();
    }
}
