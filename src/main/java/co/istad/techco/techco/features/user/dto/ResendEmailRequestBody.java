package co.istad.techco.techco.features.user.dto;

public record ResendEmailRequestBody(

        String email,

        String oldToken

) {
}
