package co.istad.techco.techco.features.cart;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.Product;
import co.istad.techco.techco.features.cart.cartitem.dto.CartItemResponse;
import co.istad.techco.techco.features.cart.dto.AddToCartRequest;
import co.istad.techco.techco.features.cart.dto.CartResponse;
import co.istad.techco.techco.features.cart.dto.RemoveCartItemRequest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface CartService {

    CartResponse addToCart(AddToCartRequest request);

    CartResponse getByUser(String userUuid);

    BasedMessage deleteByUuid(String uuid);

    BasedMessage removeQuantityByOne(String cartItemUuid);

    BasedMessage addQuantityByOne(String cartItemUuid);

    void removeCartItemByUuid(RemoveCartItemRequest request);

    Page<CartResponse> getAll(int page, int size, BigDecimal minAmount, BigDecimal maxAmount);

}
