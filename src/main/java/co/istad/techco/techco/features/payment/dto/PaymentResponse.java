package co.istad.techco.techco.features.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(

        String uuid,

        BigDecimal amount,

        String paymentMethod,

        String currency,

        String transactionStatus,

        LocalDateTime paymentDate

) {
}
