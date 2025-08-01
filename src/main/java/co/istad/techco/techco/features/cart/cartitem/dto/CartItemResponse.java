package co.istad.techco.techco.features.cart.cartitem.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CartItemResponse(

        String uuid,

        String productUuid,

        int quantity,

        BigDecimal totalPrice

) {
}
