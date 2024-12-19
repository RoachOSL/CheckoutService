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
@Table(name = "bundle_promotions")
@ToString
@Entity
public class BundlePromotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private Long firstBundleItemId;
    private Integer firstItemRequiredQuantity;
    private Long secondBundleItemId;
    private Integer secondItemRequiredQuantity;

    @Column(name = "bundlePrice", precision = 20, scale = 2)
    private BigDecimal bundlePrice;
}
