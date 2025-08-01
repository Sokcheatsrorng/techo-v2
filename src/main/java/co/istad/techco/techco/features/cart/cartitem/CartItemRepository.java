package co.istad.techco.techco.features.cart.cartitem;

import co.istad.techco.techco.domain.Cart;
import co.istad.techco.techco.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByUuid(String cartItemUuid);

    CartItem findByUuidAndCart(String s, Cart cart);
}
