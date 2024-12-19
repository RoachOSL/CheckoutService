package com.pragmaticcoders.checkout.controllers;

import com.pragmaticcoders.checkout.dtos.ItemDTO;
import com.pragmaticcoders.checkout.dtos.promotions.BundlePromotionDTO;
import com.pragmaticcoders.checkout.dtos.promotions.QuantityPromotionDTO;
import com.pragmaticcoders.checkout.entities.Item;
import com.pragmaticcoders.checkout.entities.promotions.BundlePromotion;
import com.pragmaticcoders.checkout.entities.promotions.QuantityPromotion;
import com.pragmaticcoders.checkout.services.CheckoutService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

    @InjectMocks
    private CheckoutController checkoutController;

    @Mock
    private CheckoutService checkoutService;

    @Test
    void shouldScanItemSuccessfully() {
        // Given
        Long itemId = 1L;
        doNothing().when(checkoutService).scanItem(itemId);

        // When
        ResponseEntity<String> response = checkoutController.scanItem(itemId);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        verify(checkoutService).scanItem(itemId);
    }

    @Test
    void shouldReturnTotalPriceSuccessfully() {
        // Given
        BigDecimal expectedTotal = BigDecimal.valueOf(100.00);
        when(checkoutService.calculateTotalPrice()).thenReturn(expectedTotal);

        // When
        ResponseEntity<BigDecimal> response = checkoutController.getTotalPrice();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedTotal);
        verify(checkoutService).calculateTotalPrice();
    }

    @Test
    void shouldFinalizePurchaseSuccessfully() {
        // Given
        Map<String, Object> receipt = Map.of("Total Cost", 100.00, "Total Discount", 10.00);
        when(checkoutService.finalizePurchase()).thenReturn(receipt);

        // When
        ResponseEntity<Map<String, Object>> response = checkoutController.finalizePurchase();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(receipt);
        verify(checkoutService).finalizePurchase();
    }

    @Test
    void shouldCreateNewItemSuccessfully() {
        // Given
        ItemDTO itemDTO = ItemDTO.builder()
                .name("Test Item")
                .price(BigDecimal.valueOf(10.99))
                .build();

        Item createdItem = new Item();
        createdItem.setId(1L);
        createdItem.setName("Test Item");
        createdItem.setPrice(BigDecimal.valueOf(10.99));

        when(checkoutService.createItem(itemDTO)).thenReturn(createdItem);

        // When
        ResponseEntity<Item> response = checkoutController.createItem(itemDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(createdItem);
        verify(checkoutService).createItem(itemDTO);
    }

    @Test
    void shouldCreateNewBundlePromotionSuccessfully() {
        // Given
        BundlePromotionDTO promotionDTO = BundlePromotionDTO.builder()
                .firstBundleItemId(1L)
                .firstItemRequiredQuantity(2)
                .secondBundleItemId(2L)
                .secondItemRequiredQuantity(1)
                .bundlePrice(BigDecimal.valueOf(20.00))
                .build();

        BundlePromotion createdPromotion = new BundlePromotion();
        createdPromotion.setBundlePrice(BigDecimal.valueOf(20.00));

        when(checkoutService.createBundlePromotion(promotionDTO)).thenReturn(createdPromotion);

        // When
        ResponseEntity<BundlePromotion> response = checkoutController.createBundlePromotion(promotionDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(createdPromotion);
        verify(checkoutService).createBundlePromotion(promotionDTO);
    }

    @Test
    void shouldCreateNewQuantityPromotionSuccessfully() {
        // Given
        QuantityPromotionDTO promotionDTO = QuantityPromotionDTO.builder()
                .itemId(1L)
                .requiredQuantity(3)
                .quantityPromotionPrice(BigDecimal.valueOf(15.00))
                .build();

        QuantityPromotion createdPromotion = new QuantityPromotion();
        createdPromotion.setQuantityPromotionPrice(BigDecimal.valueOf(15.00));

        when(checkoutService.createQuantityPromotion(promotionDTO)).thenReturn(createdPromotion);

        // When
        ResponseEntity<QuantityPromotion> response = checkoutController.createQuantityPromotion(promotionDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(createdPromotion);
        verify(checkoutService).createQuantityPromotion(promotionDTO);
    }
}
