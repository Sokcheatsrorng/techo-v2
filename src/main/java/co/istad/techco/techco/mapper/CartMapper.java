package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Cart;
import co.istad.techco.techco.features.cart.dto.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = CartItemMapper.class)
public interface CartMapper {


    @Mapping(target = "cartItems", source = "cartItems")
    CartResponse toCartResponse(Cart cart);

}
