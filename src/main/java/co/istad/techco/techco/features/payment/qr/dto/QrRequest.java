package co.istad.techco.techco.features.payment.qr.dto;

import jakarta.validation.constraints.NotBlank;

public record QrRequest(

        @NotBlank(message = "QR is required")
        String qr

) {
}
