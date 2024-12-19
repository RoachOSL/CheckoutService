package com.pragmaticcoders.checkout.dtos.promotions;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record QuantityPromotionDTO(

        @NotNull(message = "Item ID must not be null")
        Long itemId,

        @NotNull(message = "Required quantity must not be null")
        @Min(value = 1, message = "Required quantity must be at least 1")
        Integer requiredQuantity,

        @NotNull(message = "Quantity promotion price must not be null")
        @Min(value = 0, message = "Quantity promotion price must be a positive value")
        BigDecimal quantityPromotionPrice) {
}
