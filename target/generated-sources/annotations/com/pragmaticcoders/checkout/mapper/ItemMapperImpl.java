package com.pragmaticcoders.checkout.mapper;

import com.pragmaticcoders.checkout.dtos.ItemDTO;
import com.pragmaticcoders.checkout.entities.Item;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-19T21:06:36+0100",
    comments = "version: 1.6.2, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class ItemMapperImpl implements ItemMapper {

    @Override
    public ItemDTO toItemDTO(Item item) {
        if ( item == null ) {
            return null;
        }

        ItemDTO.ItemDTOBuilder itemDTO = ItemDTO.builder();

        itemDTO.name( item.getName() );
        itemDTO.price( item.getPrice() );

        return itemDTO.build();
    }

    @Override
    public Item toItem(ItemDTO itemDTO) {
        if ( itemDTO == null ) {
            return null;
        }

        Item item = new Item();

        item.setName( itemDTO.name() );
        item.setPrice( itemDTO.price() );

        return item;
    }
}
