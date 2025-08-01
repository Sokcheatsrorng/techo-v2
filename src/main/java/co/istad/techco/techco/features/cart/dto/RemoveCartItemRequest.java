package co.istad.techco.techco.features.cart.dto;

public record RemoveCartItemRequest(

        String cartUuid,

        String cartItemUuid

) {
}
