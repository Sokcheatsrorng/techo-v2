package co.istad.techco.techco.features.user.dto;

public record ResendForgetPasswordToken(

        String email,

        String oldToken

) {
}
