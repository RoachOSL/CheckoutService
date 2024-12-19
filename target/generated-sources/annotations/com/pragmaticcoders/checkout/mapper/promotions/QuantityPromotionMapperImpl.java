package com.pragmaticcoders.checkout.mapper.promotions;

import com.pragmaticcoders.checkout.dtos.promotions.QuantityPromotionDTO;
import com.pragmaticcoders.checkout.entities.promotions.QuantityPromotion;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-19T21:06:36+0100",
    comments = "version: 1.6.2, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class QuantityPromotionMapperImpl implements QuantityPromotionMapper {

    @Override
    public QuantityPromotionDTO toQuantityPromotionDTO(QuantityPromotion quantityPromotion) {
        if ( quantityPromotion == null ) {
            return null;
        }

        QuantityPromotionDTO.QuantityPromotionDTOBuilder quantityPromotionDTO = QuantityPromotionDTO.builder();

        quantityPromotionDTO.itemId( quantityPromotion.getItemId() );
        quantityPromotionDTO.requiredQuantity( quantityPromotion.getRequiredQuantity() );
        quantityPromotionDTO.quantityPromotionPrice( quantityPromotion.getQuantityPromotionPrice() );

        return quantityPromotionDTO.build();
    }

    @Override
    public QuantityPromotion toQuantityPromotion(QuantityPromotionDTO quantityPromotionDTO) {
        if ( quantityPromotionDTO == null ) {
            return null;
        }

        QuantityPromotion quantityPromotion = new QuantityPromotion();

        quantityPromotion.setItemId( quantityPromotionDTO.itemId() );
        quantityPromotion.setRequiredQuantity( quantityPromotionDTO.requiredQuantity() );
        quantityPromotion.setQuantityPromotionPrice( quantityPromotionDTO.quantityPromotionPrice() );

        return quantityPromotion;
    }
}
