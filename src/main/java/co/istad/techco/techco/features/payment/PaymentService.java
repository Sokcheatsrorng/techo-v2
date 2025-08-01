package co.istad.techco.techco.features.payment;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.Payment;
import co.istad.techco.techco.features.payment.dto.PaymentResponse;
import org.springframework.data.domain.Page;

public interface PaymentService {

    BasedMessage savePayment(String userUuid, String orderUuid);

    Page<PaymentResponse> getAllPayments(int page, int size, Payment.PaymentStatus status);

}
