package co.istad.techco.techco.features.user.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RegistrationResponse(

        String message,

        Integer code,

        Boolean status,

        LocalDateTime timeStamp,

        String data,

        String token,

        UserResponse user

) {
}
