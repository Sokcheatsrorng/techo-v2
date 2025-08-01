package co.istad.techco.techco.features.product.dto;

import co.istad.techco.techco.domain.ColorOption;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record ProductUpdateRequest(
        @NotBlank(message = "Product name is required")
        String name,

        String description,

        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stockQuantity,

        @NotNull(message = "Prince in is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price in must be better than zero")
        BigDecimal priceIn,

        @NotNull(message = "Price out is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price out must be better than zero")
        BigDecimal priceOut,

        Double discount,

        List<ColorOption> color,

        String thumbnail,

        String warranty,

        Boolean availability,

        List<String> images,

        @NotBlank(message = "Category uuid is required")
        String categoryUuid,

        @NotBlank(message = "Supplier uuid is required")
        String supplierUuid,

        @NotBlank(message = "Brand uuid is required")
        String brandUuid
) {
}
