package co.istad.techco.techco.features.order;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.*;
import co.istad.techco.techco.features.cart.CartRepository;
import co.istad.techco.techco.features.order.dto.*;
import co.istad.techco.techco.features.order.orderItem.dto.OrderItemResponse;
import co.istad.techco.techco.features.product.ProductRepository;
import co.istad.techco.techco.features.stock.StockRepository;
import co.istad.techco.techco.features.user.UserRepository;
import co.istad.techco.techco.mapper.OrderMapper;
import co.istad.techco.techco.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponse placeOrder(String userUuid, String cartUuid) {

        // Fetch user
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Fetch user's cart
        Cart cart = cartRepository.findByUserAndUuid(user, cartUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        // Create new order
        Order order = new Order();
        order.setUuid(Utils.generateUuid());
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        // Map CartItems to OrderItems
        List<OrderItem> orderItems = cart.getCartItems().stream().map(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setUuid(Utils.generateUuid());
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getProduct().getPriceOut());
            return orderItem;
        }).collect(Collectors.toList());

        order.setOrderItems(orderItems);

        // Calculate total amount
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);

        // Save order and order items
        orderRepository.save(order);

        // Process stock updates here
        processOrderStock(order);

        // Clear user's cart
        cart.getCartItems().clear();
        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);

        // Prepare response
        List<OrderItemResponse> orderItemResponses = orderItems.stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceAtPurchase()
                )).collect(Collectors.toList());

        return OrderResponse.builder()
                .orderUuid(order.getUuid())
                .totalAmount(order.getTotalAmount())
                .orderItems(orderItemResponses)
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .build();
    }

    @Override
    public List<OrderResponse> getOrdersByUser(String userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<Order> orders = orderRepository.findByUser(user);

        return orders.stream().map(orderMapper::toOrderResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BasedMessage updateOrderStatus(String orderUuid, String status) {
        Order order = orderRepository.findByUuid(orderUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        try {
            order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);
            return new BasedMessage("Order status updated to " + status);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid order status");
        }
    }

    @Override
    @Transactional
    public BasedMessage cancelOrder(String orderUuid) {
        Order order = orderRepository.findByUuid(orderUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        // Prevent canceling already canceled or shipped/delivered orders
        if (order.getStatus() == Order.OrderStatus.CANCELED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order is already canceled");
        }
        if (order.getStatus() == Order.OrderStatus.SHIPPED || order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel shipped or delivered orders");
        }

        // Update order status
        order.setStatus(Order.OrderStatus.CANCELED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        // Restore stock in bulk
        List<Product> updatedProducts = order.getOrderItems().stream().map(orderItem -> {
            Product product = orderItem.getProduct();

            if (product == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found for order item");
            }

            product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            product.setUpdatedAt(LocalDateTime.now());
            return product;
        }).collect(Collectors.toList());
        productRepository.saveAll(updatedProducts);

        // Create stock return records
        List<Stock> newStocks = order.getOrderItems().stream().map(orderItem -> {
            Stock stock = new Stock();
            stock.setUuid(Utils.generateUuid());
            stock.setProduct(orderItem.getProduct());
            stock.setQuantityDelta(orderItem.getQuantity());
            stock.setReason(Stock.Reason.RETURNED);
            stock.setAvailability(orderItem.getProduct().getAvailability());
            return stock;
        }).collect(Collectors.toList());
        stockRepository.saveAll(newStocks);

        return new BasedMessage("Order canceled successfully");
    }

    @Override
    @Transactional
    public void processOrderStock(Order order) {

        for (OrderItem orderItem : order.getOrderItems()) {

            Product product = orderItem.getProduct();

            // Update product stock quantity
            int newQuantity = product.getStockQuantity() - orderItem.getQuantity();
            if (newQuantity < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient stock for product: " + product.getName());
            }
            product.setStockQuantity(newQuantity);
            productRepository.save(product);

            // Create Stock record
            Stock stock = new Stock();
            stock.setUuid(Utils.generateUuid());
            stock.setProduct(product);
            stock.setQuantityDelta(-orderItem.getQuantity()); // Negative for SOLD
            stock.setReason(Stock.Reason.SOLD);

            stockRepository.save(stock);
        }
    }

    @Override
    public OrderResponse getByUuid(String uuid, String userUuid) {

        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User has not been found"
                        )
                );

        return orderMapper.toOrderResponse(
                orderRepository.findByUuidAndUser(uuid, user)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Order has not been found with the user"
                                )
                        )
        );
    }
}
