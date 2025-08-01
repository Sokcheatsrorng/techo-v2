package co.istad.techco.techco.features.cart;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.Cart;
import co.istad.techco.techco.domain.CartItem;
import co.istad.techco.techco.domain.Product;
import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.features.cart.cartitem.CartItemRepository;
import co.istad.techco.techco.features.cart.cartitem.dto.CartItemResponse;
import co.istad.techco.techco.features.cart.dto.AddToCartRequest;
import co.istad.techco.techco.features.cart.dto.CartResponse;
import co.istad.techco.techco.features.cart.dto.RemoveCartItemRequest;
import co.istad.techco.techco.features.product.ProductRepository;
import co.istad.techco.techco.features.user.UserRepository;
import co.istad.techco.techco.mapper.CartMapper;
import co.istad.techco.techco.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {

        if (request.quantity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
        }

        // Fetch user
        User user = userRepository.findByUuid(request.userUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Fetch product
        Product product = productRepository.findByUuid(request.productUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        // Find or create cart
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUuid(Utils.generateUuid());
                    newCart.setUser(user);
                    newCart.setCreatedAt(LocalDateTime.now());
                    newCart.setCartItems(new ArrayList<>());
                    newCart.setTotalAmount(BigDecimal.ZERO);
                    return cartRepository.save(newCart);
                });

        // Check if product is already in the cart
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> request.productUuid().equals(item.getProduct().getUuid()))
                .findFirst()
                .orElse(null);

        BigDecimal discountedPrice = applyDiscount(product.getPriceOut(), product.getDiscount());

        if (cartItem != null) {
            // Update quantity
            cartItem.setQuantity(cartItem.getQuantity() + request.quantity());
            cartItem.setTotalPrice(discountedPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItemRepository.save(cartItem);
        } else {
            // Add new cart item
            cartItem = new CartItem();
            cartItem.setUuid(Utils.generateUuid());
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.quantity());
            cartItem.setAddedAt(LocalDateTime.now());
            cartItem.setTotalPrice(discountedPrice.multiply(BigDecimal.valueOf(request.quantity())));
            cart.getCartItems().add(cartItem);

            cartItemRepository.save(cartItem);
        }

        // Calculate total cart amount
        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);

        // Prepare cart items response
        List<CartItemResponse> cartItemResponses = cart.getCartItems().stream()
                .map(item -> new CartItemResponse(
                        item.getUuid(),
                        item.getProduct().getUuid(),
                        item.getQuantity(),
                        item.getTotalPrice()
                ))
                .toList();

        // Return full cart response
        return CartResponse.builder()
                .uuid(cart.getUuid())
                .totalAmount(cart.getTotalAmount())
                .cartItems(cartItemResponses)
                .build();
    }

    private BigDecimal applyDiscount(BigDecimal price, Double discount) {
        if (discount != null && discount > 0) {
            BigDecimal discountMultiplier = BigDecimal.valueOf(1 - (discount / 100));
            return price.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
        }
        return price;
    }

    @Override
    public CartResponse getByUser(String userUuid) {

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));


        return cartMapper.toCartResponse(cart);
    }

    @Transactional
    @Override
    public BasedMessage deleteByUuid(String uuid) {
        if (!cartRepository.existsByUuid(uuid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found");
        }
        cartRepository.deleteByUuid(uuid);
        return new BasedMessage("Cart deleted successfully");
    }

    @Override
    @Transactional
    public BasedMessage removeQuantityByOne(String cartItemUuid) {
        CartItem cartItem = cartItemRepository.findByUuid(cartItemUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        int updatedQuantity = cartItem.getQuantity() - 1;

        if (updatedQuantity > 0) {
            cartItem.setQuantity(updatedQuantity);
            BigDecimal discountedPrice = applyDiscount(cartItem.getProduct().getPriceOut(), cartItem.getProduct().getDiscount());
            cartItem.setTotalPrice(discountedPrice.multiply(BigDecimal.valueOf(updatedQuantity)));
            cartItemRepository.save(cartItem);
        } else {
            Cart cart = cartItem.getCart();
            cart.getCartItems().remove(cartItem);
            cartItemRepository.delete(cartItem);
        }

        // Update cart total amount
        Cart cart = cartItem.getCart();
        BigDecimal updatedTotal = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(updatedTotal);
        cartRepository.save(cart);

        return new BasedMessage(updatedQuantity > 0 ? "Quantity decreased by one." : "Cart item removed as quantity reached zero.");
    }

    @Override
    @Transactional
    public BasedMessage addQuantityByOne(String cartItemUuid) {
        CartItem cartItem = cartItemRepository.findByUuid(cartItemUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found"));

        int updatedQuantity = cartItem.getQuantity() + 1;
        cartItem.setQuantity(updatedQuantity);

        BigDecimal discountedPrice = applyDiscount(cartItem.getProduct().getPriceOut(), cartItem.getProduct().getDiscount());
        cartItem.setTotalPrice(discountedPrice.multiply(BigDecimal.valueOf(updatedQuantity)));
        cartItemRepository.save(cartItem);

        // Update cart total amount
        Cart cart = cartItem.getCart();
        BigDecimal updatedTotal = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(updatedTotal);
        cartRepository.save(cart);

        return new BasedMessage("Quantity increased by one.");
    }

    @Override
    @Transactional
    public void removeCartItemByUuid(RemoveCartItemRequest request) {

        Cart cart = cartRepository.findByUuid(request.cartUuid())
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Cart has not been found"
                        )
                );

        CartItem cartItem = cartItemRepository.findByUuidAndCart(request.cartItemUuid(), cart);

        if (cartItem == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cart item has not been found"
            );
        }

        BigDecimal updatedTotalAmount = cart.getTotalAmount().subtract(cartItem.getTotalPrice());

        cart.setTotalAmount(updatedTotalAmount.max(BigDecimal.ZERO));
        cart.getCartItems().remove(cartItem);

        cartRepository.save(cart);

        cartItemRepository.delete(cartItem);
    }

    @Override
    public Page<CartResponse> getAll(int page, int size, BigDecimal minAmount, BigDecimal maxAmount) {
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page must be greater than or equal to 0");
        }
        if (size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size must be greater than or equal to 1");
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Cart> carts;

        if (minAmount != null && maxAmount != null) {
            carts = cartRepository.findByTotalAmountBetween(minAmount, maxAmount, pageRequest);
        } else if (minAmount != null) {
            carts = cartRepository.findByTotalAmountGreaterThanEqual(minAmount, pageRequest);
        } else if (maxAmount != null) {
            carts = cartRepository.findByTotalAmountLessThanEqual(maxAmount, pageRequest);
        } else {
            carts = cartRepository.findAll(pageRequest);
        }

        return carts.map(cartMapper::toCartResponse);
    }

}
