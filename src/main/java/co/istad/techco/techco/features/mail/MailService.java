package co.istad.techco.techco.features.mail;

import co.istad.techco.techco.domain.Order;
import co.istad.techco.techco.domain.Payment;
import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.features.mail.dto.MailRequest;

public interface MailService {

    void sendEmail(MailRequest request);

    void sendTokenForEmailVerification(User user, String verificationToken);

    void welcomeMessage(User user);

    void sendTokenForForgetPassword(User user, String resetToken);

    void sendPaymentReceipt(User user, Payment payment, Order order);

}
