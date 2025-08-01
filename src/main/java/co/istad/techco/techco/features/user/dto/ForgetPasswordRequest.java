package co.istad.techco.techco.features.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgetPasswordRequest(

        @Email
        @NotBlank
        String email

) {
}
