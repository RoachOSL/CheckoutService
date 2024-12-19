package com.pragmaticcoders.checkout.repositories.promotions;

import com.pragmaticcoders.checkout.entities.promotions.QuantityPromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuantityPromotionRepository extends JpaRepository<QuantityPromotion, Long> {

    List<QuantityPromotion> findByItemId(Long itemId);
}
