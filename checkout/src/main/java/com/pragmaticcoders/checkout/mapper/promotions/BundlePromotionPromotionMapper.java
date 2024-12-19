package com.pragmaticcoders.checkout.mapper.promotions;

import com.pragmaticcoders.checkout.dtos.promotions.BundlePromotionDTO;
import com.pragmaticcoders.checkout.entities.promotions.BundlePromotion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BundlePromotionPromotionMapper {

    BundlePromotionDTO toBundlePromotionDTO(BundlePromotion bundlePromotion);

    BundlePromotion toBundlePromotion(BundlePromotionDTO bundlePromotionDTO);
}
