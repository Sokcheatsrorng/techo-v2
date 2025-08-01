package co.istad.techco.techco.features.brand.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BrandRequest(

        @NotBlank(message = "Brand name is required")
        @Size(min = 2, max = 50, message = "Brand name must be between 2 and 50 characters")
        String name,

        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description,

        @Pattern(
                regexp = "^(https?://.*\\.(?:png|jpg|jpeg|gif|svg|webp))$",
                message = "Brand logo must be a valid image URL (png, jpg, jpeg, gif, svg, webp)"
        )
        String brandLogo

) {}
