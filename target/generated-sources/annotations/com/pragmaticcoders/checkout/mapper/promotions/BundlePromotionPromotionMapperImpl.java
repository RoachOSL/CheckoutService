package com.pragmaticcoders.checkout.mapper.promotions;

import com.pragmaticcoders.checkout.dtos.promotions.BundlePromotionDTO;
import com.pragmaticcoders.checkout.entities.promotions.BundlePromotion;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-19T21:06:36+0100",
    comments = "version: 1.6.2, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class BundlePromotionPromotionMapperImpl implements BundlePromotionPromotionMapper {

    @Override
    public BundlePromotionDTO toBundlePromotionDTO(BundlePromotion bundlePromotion) {
        if ( bundlePromotion == null ) {
            return null;
        }

        BundlePromotionDTO.BundlePromotionDTOBuilder bundlePromotionDTO = BundlePromotionDTO.builder();

        bundlePromotionDTO.firstBundleItemId( bundlePromotion.getFirstBundleItemId() );
        bundlePromotionDTO.firstItemRequiredQuantity( bundlePromotion.getFirstItemRequiredQuantity() );
        bundlePromotionDTO.secondBundleItemId( bundlePromotion.getSecondBundleItemId() );
        bundlePromotionDTO.secondItemRequiredQuantity( bundlePromotion.getSecondItemRequiredQuantity() );
        bundlePromotionDTO.bundlePrice( bundlePromotion.getBundlePrice() );

        return bundlePromotionDTO.build();
    }

    @Override
    public BundlePromotion toBundlePromotion(BundlePromotionDTO bundlePromotionDTO) {
        if ( bundlePromotionDTO == null ) {
            return null;
        }

        BundlePromotion bundlePromotion = new BundlePromotion();

        bundlePromotion.setFirstBundleItemId( bundlePromotionDTO.firstBundleItemId() );
        bundlePromotion.setFirstItemRequiredQuantity( bundlePromotionDTO.firstItemRequiredQuantity() );
        bundlePromotion.setSecondBundleItemId( bundlePromotionDTO.secondBundleItemId() );
        bundlePromotion.setSecondItemRequiredQuantity( bundlePromotionDTO.secondItemRequiredQuantity() );
        bundlePromotion.setBundlePrice( bundlePromotionDTO.bundlePrice() );

        return bundlePromotion;
    }
}
