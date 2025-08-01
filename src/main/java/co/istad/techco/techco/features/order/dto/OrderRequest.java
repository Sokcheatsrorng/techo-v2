package co.istad.techco.techco.features.order.dto;

import co.istad.techco.techco.features.order.orderItem.dto.OrderItemRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import java.util.List;

public record OrderRequest(

        @NotBlank(message = "User UUID is required")
        @Pattern(
                regexp = "^[a-fA-F0-9\\-]{36}$",
                message = "Invalid UUID format for user"
        )
        String userUuid,

        @NotEmpty(message = "Order must contain at least one item")
        List<@Valid OrderItemRequest> items,

        @NotBlank(message = "Payment method is required")
        @Pattern(
                regexp = "^(CASH|CREDIT_CARD|BAKONG)$",
                message = "Payment method must be one of: CASH, CREDIT_CARD, BAKONG"
        )
        String paymentMethod

) {
}
