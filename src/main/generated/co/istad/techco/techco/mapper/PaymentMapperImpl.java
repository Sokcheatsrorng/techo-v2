package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Payment;
import co.istad.techco.techco.features.payment.dto.PaymentResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-03T22:58:19+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentResponse toPaymentResponse(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        String uuid = null;
        BigDecimal amount = null;
        String paymentMethod = null;
        String currency = null;
        String transactionStatus = null;
        LocalDateTime paymentDate = null;

        uuid = payment.getUuid();
        amount = payment.getAmount();
        if ( payment.getPaymentMethod() != null ) {
            paymentMethod = payment.getPaymentMethod().name();
        }
        if ( payment.getCurrency() != null ) {
            currency = payment.getCurrency().name();
        }
        if ( payment.getTransactionStatus() != null ) {
            transactionStatus = payment.getTransactionStatus().name();
        }
        paymentDate = payment.getPaymentDate();

        PaymentResponse paymentResponse = new PaymentResponse( uuid, amount, paymentMethod, currency, transactionStatus, paymentDate );

        return paymentResponse;
    }
}
