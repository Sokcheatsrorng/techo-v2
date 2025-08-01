package co.istad.techco.techco.features.user.dto;

import co.istad.techco.techco.features.address.dto.AddressRequest;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record RegistrationRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 25, message = "Username must be between 3 and 25 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$",
                message = "Username can only contain letters, numbers, dots (.), underscores (_), and dashes (-)"
        )
        String username,

//        @NotBlank(message = "Phone number is required")
//        @Size(min = 8, max = 15, message = "Phone number must be between 8 and 15 digits")
        String phoneNumber,

        @Valid // Ensure AddressRequest is also validated
        AddressRequest address,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)"
        )
        String password,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)"
        )
        String confirmPassword,

        @Nullable
        @Pattern(
                regexp = "^https://.*$",
                message = "Profile must be a valid URL starting with https"
        )
        String profile

) {
}
