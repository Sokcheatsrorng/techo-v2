package co.istad.techco.techco.features.payment;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.Payment;
import co.istad.techco.techco.features.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{userUuid}/{orderUuid}")
    public BasedMessage savePayment(
            @PathVariable String userUuid,
            @PathVariable String orderUuid) {

        return paymentService.savePayment(userUuid, orderUuid);

    }

    @GetMapping
    public Page<PaymentResponse> getAllPayments(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "12") int size,
            @RequestParam(required = false, defaultValue = "")Payment.PaymentStatus status
            ) {

        return paymentService.getAllPayments(page, size, status);

    }

}
