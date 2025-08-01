package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Cart;
import co.istad.techco.techco.domain.CartItem;
import co.istad.techco.techco.features.cart.cartitem.dto.CartItemResponse;
import co.istad.techco.techco.features.cart.dto.CartResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-03T22:58:19+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Override
    public CartResponse toCartResponse(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        CartResponse.CartResponseBuilder cartResponse = CartResponse.builder();

        cartResponse.cartItems( cartItemListToCartItemResponseList( cart.getCartItems() ) );
        cartResponse.uuid( cart.getUuid() );
        cartResponse.totalAmount( cart.getTotalAmount() );

        return cartResponse.build();
    }

    protected List<CartItemResponse> cartItemListToCartItemResponseList(List<CartItem> list) {
        if ( list == null ) {
            return null;
        }

        List<CartItemResponse> list1 = new ArrayList<CartItemResponse>( list.size() );
        for ( CartItem cartItem : list ) {
            list1.add( cartItemMapper.toCartItemResponse( cartItem ) );
        }

        return list1;
    }
}
