package com.pragmaticcoders.checkout.repositories.promotions;

import com.pragmaticcoders.checkout.entities.promotions.BundlePromotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BundlePromotionRepository extends JpaRepository<BundlePromotion, Long> {

    List<BundlePromotion> findByFirstBundleItemId(Long firstBundleItemId);

    List<BundlePromotion> findBySecondBundleItemId(Long secondBundleItemId);
}
