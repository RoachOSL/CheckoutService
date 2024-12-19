package com.pragmaticcoders.checkout.controllers;

import com.pragmaticcoders.checkout.dtos.ItemDTO;
import com.pragmaticcoders.checkout.dtos.promotions.BundlePromotionDTO;
import com.pragmaticcoders.checkout.dtos.promotions.QuantityPromotionDTO;
import com.pragmaticcoders.checkout.entities.Item;
import com.pragmaticcoders.checkout.entities.promotions.BundlePromotion;
import com.pragmaticcoders.checkout.entities.promotions.QuantityPromotion;
import com.pragmaticcoders.checkout.services.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RequestMapping("/checkout")
@RequiredArgsConstructor
@RestController
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    /**
     * POST /checkout/scan?itemId={itemId}
     * Scans the given item by its ID.
     */
    @PostMapping("/scan")
    public ResponseEntity<String> scanItem(@RequestParam Long itemId) {
        checkoutService.scanItem(itemId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /**
     * GET /checkout/total
     * Returns the total price of all scanned items as a BigDecimal.
     */
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalPrice() {
        BigDecimal total = checkoutService.calculateTotalPrice();
        return ResponseEntity.ok(total);
    }

    /**
     * POST /checkout/finalize
     * Finalizes the purchase and clears the cart.
     */
    @PostMapping("/finalize")
    public ResponseEntity<Map<String, Object>> finalizePurchase() {
        Map<String, Object> receipt = checkoutService.finalizePurchase();

        return ResponseEntity.ok(receipt);
    }

    /**
     * POST /checkout/item
     * Creates a new item in the database.
     */
    @PostMapping("/item")
    public ResponseEntity<Item> createItem(@Valid @RequestBody ItemDTO itemDTO) {
        Item createdItem = checkoutService.createItem(itemDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem);
    }

    /**
     * POST /checkout/bundle_promotions
     * Creates a new bundle promotion in the database.
     */
    @PostMapping("/bundle_promotions")
    public ResponseEntity<BundlePromotion> createBundlePromotion(
            @Valid @RequestBody BundlePromotionDTO bundlePromotionDTO) {
        BundlePromotion createdPromotion = checkoutService.createBundlePromotion(bundlePromotionDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
    }

    /**
     * POST /checkout/quantity_promotions
     * Creates a new quantity promotion in the database.
     */
    @PostMapping("/quantity_promotions")
    public ResponseEntity<QuantityPromotion> createQuantityPromotion(
            @Valid @RequestBody QuantityPromotionDTO quantityPromotionDTO) {
        QuantityPromotion createdPromotion = checkoutService.createQuantityPromotion(quantityPromotionDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
    }
}
