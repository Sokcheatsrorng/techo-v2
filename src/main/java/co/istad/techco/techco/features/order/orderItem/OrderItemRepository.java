package co.istad.techco.techco.features.order.orderItem;

import co.istad.techco.techco.domain.Order;
import co.istad.techco.techco.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<List<OrderItem>> findByOrder(Order order);
}
