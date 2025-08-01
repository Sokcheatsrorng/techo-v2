package co.istad.techco.techco.features.cart.dto;

import co.istad.techco.techco.features.cart.cartitem.dto.CartItemResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record CartResponse(

        String uuid,

        BigDecimal totalAmount,

        List<CartItemResponse> cartItems

) {
}
