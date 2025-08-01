package co.istad.techco.techco.features.mail.dto;

import lombok.Builder;

@Builder
public record MailRequest(

        String from,

        String senderName,

        String to,

        String subject,

        String text

) {
}
