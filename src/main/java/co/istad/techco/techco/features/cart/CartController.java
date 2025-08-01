package co.istad.techco.techco.features.cart;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.features.cart.dto.AddToCartRequest;
import co.istad.techco.techco.features.cart.dto.CartResponse;
import co.istad.techco.techco.features.cart.dto.RemoveCartItemRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add-to-cart")
    public CartResponse addToCart(@Valid @RequestBody AddToCartRequest addToCartRequest) {

        return cartService.addToCart(addToCartRequest);

    }

    @GetMapping
    public Page<CartResponse> getAll(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "12") Integer size,
            @RequestParam(name = "minAmount", required = false)BigDecimal minAmount,
            @RequestParam(name = "maxAmount", required = false) BigDecimal maxAmount
            ) {

        return cartService.getAll(page, size, minAmount, maxAmount);

    }

    @GetMapping("/get-by-user/{userUuid}")
    public CartResponse getCartByUser(@PathVariable(name = "userUuid") String userUuid) {

        return cartService.getByUser(userUuid);

    }

    @DeleteMapping("/{uuid}")
    public BasedMessage deleteCartByUuid(@PathVariable(name = "uuid") String uuid) {

        return cartService.deleteByUuid(uuid);

    }

    @PutMapping("/remove-quantity/{cartItemUuid}")
    public BasedMessage removeQuantityByOne(@PathVariable(name = "cartItemUuid") String cartItemUuid) {

        return cartService.removeQuantityByOne(cartItemUuid);

    }

    @PutMapping("/add-quantity/{cartItemUuid}")
    public BasedMessage addQuantityByOne(@PathVariable(name = "cartItemUuid") String cartItemUuid) {

        return cartService.addQuantityByOne(cartItemUuid);
    }

    @PutMapping("/remove-cart-item")
    public BasedMessage remoteCartItemByUuid(@Valid @RequestBody RemoveCartItemRequest request) {

        cartService.removeCartItemByUuid(request);

        return new BasedMessage("Cart item has been removed");
    }

}
