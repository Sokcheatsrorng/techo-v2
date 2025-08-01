package co.istad.techco.techco.features.media.dto;

import jakarta.validation.constraints.NotBlank;

public record MediaRequest(

        @NotBlank(message = "Media is required")
        String media

) {
}
