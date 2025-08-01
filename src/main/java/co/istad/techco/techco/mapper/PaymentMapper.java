package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Payment;
import co.istad.techco.techco.features.payment.dto.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    PaymentResponse toPaymentResponse(Payment payment);

}
