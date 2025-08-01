package co.istad.techco.techco.features.cart;

import co.istad.techco.techco.domain.Cart;
import co.istad.techco.techco.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;


public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

    void deleteByUuid(String uuid);

    boolean existsByUuid(String uuid);

    Optional<Cart> findByUserAndUuid(User user, String cartUuid);

    Optional<Cart> findByUuid(String s);

    Page<Cart> findByTotalAmountBetween(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);

    Page<Cart> findByTotalAmountGreaterThanEqual(BigDecimal minAmount, Pageable pageable);

    Page<Cart> findByTotalAmountLessThanEqual(BigDecimal maxAmount, Pageable pageable);

}
