package com.pragmaticcoders.checkout.entities.promotions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Table(name = "quantity_promotions")
@ToString
@Entity
public class QuantityPromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "item_id")
    private Long itemId;

    private Integer requiredQuantity;

    @Column(name = "quantity_promotion_price", precision = 20, scale = 2)
    private BigDecimal quantityPromotionPrice;
}
