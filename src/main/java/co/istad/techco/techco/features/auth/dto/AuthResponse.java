package co.istad.techco.techco.features.auth.dto;

import co.istad.techco.techco.features.user.dto.UserResponse;
import lombok.Builder;

@Builder
public record AuthResponse(

        String type,

        String accessToken,

        String refreshToken,

        UserResponse user

) {
}
