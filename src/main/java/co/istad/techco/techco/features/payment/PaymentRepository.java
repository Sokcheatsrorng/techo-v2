package co.istad.techco.techco.features.payment;

import co.istad.techco.techco.domain.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByTransactionStatus(Payment.PaymentStatus status, Pageable pageable);

}
