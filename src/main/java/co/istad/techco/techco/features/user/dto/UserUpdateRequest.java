package co.istad.techco.techco.features.user.dto;

import co.istad.techco.techco.features.address.dto.AddressRequest;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record UserUpdateRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$",
                message = "Username can only contain letters, numbers, dots (.), underscores (_) and dashes (-)"
        )
        String username,

//        @Pattern(
//                regexp = "^\\+?[0-9]{10,15}$",
//                message = "Phone number must be a valid format (10-15 digits, optional leading +)"
//        )
        String phoneNumber,

//        @Nullable
//        @Pattern(
//                regexp = "^(https?://.*\\.(?:png|jpg|jpeg|gif|svg|webp))$",
//                message = "Profile must be a valid image URL (png, jpg, jpeg, gif, svg, webp)"
//        )
        String profile,

        @Valid // Ensures AddressRequest is also validated
        AddressRequest address

) {
}
