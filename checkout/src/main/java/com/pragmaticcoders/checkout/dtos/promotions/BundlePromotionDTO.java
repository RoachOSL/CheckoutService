package com.pragmaticcoders.checkout.dtos.promotions;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BundlePromotionDTO(

        @NotNull(message = "First bundle item ID must not be null")
        Long firstBundleItemId,

        @NotNull(message = "First item required quantity must not be null")
        @Min(value = 1, message = "First item required quantity must be at least 1")
        Integer firstItemRequiredQuantity,

        @NotNull(message = "Second bundle item ID must not be null")
        Long secondBundleItemId,

        @NotNull(message = "Second item required quantity must not be null")
        @Min(value = 1, message = "Second item required quantity must be at least 1")
        Integer secondItemRequiredQuantity,

        @NotNull(message = "Bundle price must not be null")
        @Min(value = 0, message = "Bundle price must be a positive value")
        BigDecimal bundlePrice) {
}
