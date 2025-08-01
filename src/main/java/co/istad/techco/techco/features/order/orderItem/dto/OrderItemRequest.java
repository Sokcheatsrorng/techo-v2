package co.istad.techco.techco.features.order.orderItem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record OrderItemRequest(

        @NotBlank(message = "Product UUID is required")
        @Pattern(
                regexp = "^[a-fA-F0-9\\-]{36}$",
                message = "Invalid UUID format for product"
        )
        String productUuid,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity

) {}
