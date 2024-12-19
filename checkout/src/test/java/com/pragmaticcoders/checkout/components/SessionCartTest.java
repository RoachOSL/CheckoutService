package com.pragmaticcoders.checkout.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SessionCartTest {

    private static final Long ITEM_ID = 1L;
    private static final int INITIAL_QUANTITY = 1;
    private static final BigDecimal DEFAULT_DISCOUNT = BigDecimal.TEN;
    private static final int BUNDLE_QUANTITY = 3;
    private SessionCart sessionCart;

    @BeforeEach
    void setUp() {
        sessionCart = new SessionCart();
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 3L})
    void shouldAddItemToScannedItems(Long itemId) {
        // Given
        int initialQuantity = INITIAL_QUANTITY;

        // When
        sessionCart.addItem(itemId, initialQuantity);

        // Then
        assertThat(sessionCart.getScannedItems()).containsEntry(itemId, initialQuantity);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void shouldAccumulateQuantityForSameItem(int additionalQuantity) {
        // Given
        sessionCart.addItem(ITEM_ID, INITIAL_QUANTITY);

        // When
        sessionCart.addItem(ITEM_ID, additionalQuantity);

        // Then
        assertThat(sessionCart.getScannedItems()).containsEntry(ITEM_ID, INITIAL_QUANTITY + additionalQuantity);
    }

    @ParameterizedTest
    @ValueSource(doubles = {10.0, 20.0, 30.5})
    void shouldAddDiscountForItem(double discountAmount) {
        // Given
        BigDecimal discount = BigDecimal.valueOf(discountAmount);

        // When
        sessionCart.addDiscount(ITEM_ID, discount);

        // Then
        assertThat(sessionCart.getDiscount(ITEM_ID)).isEqualByComparingTo(discount);
    }

    @Test
    void shouldReturnZeroDiscountForItemWithoutDiscount() {
        // Given/When
        BigDecimal discount = sessionCart.getDiscount(ITEM_ID);

        // Then
        assertThat(discount).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 5, 10})
    void shouldAddBundleItem(int quantity) {
        // Given
        BigDecimal discount = DEFAULT_DISCOUNT;

        // When
        sessionCart.addBundleItem(ITEM_ID, quantity, discount);

        // Then
        assertThat(sessionCart.getBundleItems()).containsEntry(ITEM_ID, quantity);
    }

    @Test
    void shouldClearAllData() {
        // Given
        sessionCart.addItem(ITEM_ID, INITIAL_QUANTITY);
        sessionCart.addDiscount(ITEM_ID, DEFAULT_DISCOUNT);
        sessionCart.addBundleItem(ITEM_ID, BUNDLE_QUANTITY, DEFAULT_DISCOUNT);

        // When
        sessionCart.clear();

        // Then
        assertThat(sessionCart.getScannedItems()).isEmpty();
        assertThat(sessionCart.getDiscounts()).isEmpty();
        assertThat(sessionCart.getBundleItems()).isEmpty();
    }
}
