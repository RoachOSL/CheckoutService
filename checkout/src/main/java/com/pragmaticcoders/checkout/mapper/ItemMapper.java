package com.pragmaticcoders.checkout.mapper;

import com.pragmaticcoders.checkout.dtos.ItemDTO;
import com.pragmaticcoders.checkout.entities.Item;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDTO toItemDTO(Item item);

    Item toItem(ItemDTO itemDTO);
}
