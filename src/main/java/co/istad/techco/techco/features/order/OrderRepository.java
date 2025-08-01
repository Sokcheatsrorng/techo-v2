package co.istad.techco.techco.features.order;

import co.istad.techco.techco.domain.Order;
import co.istad.techco.techco.domain.User;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;

import java.beans.JavaBean;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    List<Order> findByUser(User user);

    Optional<Order> findByUuid(String orderUuid);

    Optional<Order> findByUserAndUuid(User user, String orderUuid);

    Optional<Order> findByUuidAndUser(String uuid, User user);
}
