package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.CartItem;
import co.istad.techco.techco.domain.Product;
import co.istad.techco.techco.features.cart.cartitem.dto.CartItemResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-03T22:58:19+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class CartItemMapperImpl implements CartItemMapper {

    @Override
    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemResponse.CartItemResponseBuilder cartItemResponse = CartItemResponse.builder();

        cartItemResponse.totalPrice( cartItem.getTotalPrice() );
        cartItemResponse.productUuid( cartItemProductUuid( cartItem ) );
        cartItemResponse.uuid( cartItem.getUuid() );
        if ( cartItem.getQuantity() != null ) {
            cartItemResponse.quantity( cartItem.getQuantity() );
        }

        return cartItemResponse.build();
    }

    private String cartItemProductUuid(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        Product product = cartItem.getProduct();
        if ( product == null ) {
            return null;
        }
        String uuid = product.getUuid();
        if ( uuid == null ) {
            return null;
        }
        return uuid;
    }
}
