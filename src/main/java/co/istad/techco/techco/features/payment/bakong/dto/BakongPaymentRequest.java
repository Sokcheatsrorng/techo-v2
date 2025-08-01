package co.istad.techco.techco.features.payment.bakong.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BakongPaymentRequest(

        @NotBlank(message = "User UUID is required")
        @Pattern(
                regexp = "^[a-fA-F0-9\\-]{36}$",
                message = "Invalid UUID format for user"
        )
        String userUuid,

        @NotBlank(message = "Order UUID is required")
        @Pattern(
                regexp = "^[a-fA-F0-9\\-]{36}$",
                message = "Invalid UUID format for order"
        )
        String orderUuid,

        @NotBlank(message = "Currency is required")
        @Pattern(
                regexp = "^(KHR|USD)$",
                message = "Currency must be either 'KHR' or 'USD'"
        )
        String currency,

        @NotBlank(message = "City is required")
        @Size(min = 2, max = 50, message = "City name must be between 2 and 50 characters")
        String city

) {
}
