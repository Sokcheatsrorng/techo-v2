package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.CartItem;
import co.istad.techco.techco.features.cart.cartitem.dto.CartItemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "totalPrice", source = "totalPrice")
    @Mapping(target = "productUuid", source = "product.uuid")
    CartItemResponse toCartItemResponse(CartItem cartItem);

}
