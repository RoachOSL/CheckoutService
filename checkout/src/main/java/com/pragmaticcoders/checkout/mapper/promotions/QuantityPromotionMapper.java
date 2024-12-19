package com.pragmaticcoders.checkout.mapper.promotions;

import com.pragmaticcoders.checkout.dtos.promotions.QuantityPromotionDTO;
import com.pragmaticcoders.checkout.entities.promotions.QuantityPromotion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuantityPromotionMapper {

    QuantityPromotionDTO toQuantityPromotionDTO(QuantityPromotion quantityPromotion);

    QuantityPromotion toQuantityPromotion(QuantityPromotionDTO quantityPromotionDTO);
}
