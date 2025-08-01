package co.istad.techco.techco.features.mail;

import co.istad.techco.techco.domain.Order;
import co.istad.techco.techco.domain.OrderItem;
import co.istad.techco.techco.domain.Payment;
import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.features.mail.dto.MailRequest;
import co.istad.techco.techco.features.order.dto.OrderResponse;
import co.istad.techco.techco.features.order.orderItem.OrderItemRepository;
import co.istad.techco.techco.features.order.orderItem.dto.OrderItemResponse;
import co.istad.techco.techco.mapper.OrderItemMapper;
import co.istad.techco.techco.mapper.OrderMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${spring.mail.senderName}")
    private String senderName;

    @Value("${client-path}")
    private String clientUrl;

    @Override
    public void sendTokenForEmailVerification(User user, String verificationToken) {
        String subject = "Email Verification";
        String mailContent = "<div style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                "<p style=\"font-size: 16px;\">Hi <strong>" + user.getUsername() + "</strong>,</p>" +
                "<p style=\"font-size: 14px;\">Thank you for registering with us! We are excited to have you on board. Please follow the link below to complete your registration and activate your account:</p>" +
                "<p style=\"font-size: 14px; color: #555;\">Here is your verification token:</p>" +
                "<p style=\"font-size: 18px; color: #000; font-weight: bold;\">" + verificationToken + "</p>" +
                "<p style=\"text-align: center; margin: 20px 0;\">" +
                "<a href=\"" + clientUrl + "\" style=\"display: inline-block; padding: 10px 20px; font-size: 16px; color: #fff; background-color: #007BFF; text-decoration: none; border-radius: 5px;\">Verify Your Email</a>" +
                "</p>" +
                "<p style=\"font-size: 14px;\">If you have any questions, feel free to reach out to us.</p>" +
                "<p style=\"font-size: 14px;\">Thank you,<br><strong>Team</strong></p>" +
                "</div>";

        MailRequest request = MailRequest.builder()
                .from(mailFrom)
                .senderName(senderName)
                .to(user.getEmail())
                .subject(subject)
                .text(mailContent)
                .build();

        sendEmail(request);
    }

    @Override
    public void sendEmail(MailRequest request) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper;

        try {
            messageHelper = new MimeMessageHelper(mimeMessage, true); // true enables multipart (HTML support)
            messageHelper.setFrom(mailFrom, senderName);
            messageHelper.setTo(request.to());
            messageHelper.setSubject(request.subject());
            messageHelper.setText(request.text(), true);

            mailSender.send(mimeMessage);
            log.info("Email sent successfully to {}", request.to());
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Error sending email to {}: {}", request.to(), e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void welcomeMessage(User user) {
        String subject = "Welcome to the iDonate Platform!";
        String mailContent = "<p>Hi " + user.getUsername() + ",</p>" +
                "<p>Thank you for joining our platform. We’re excited to have you on board.</p>" +
                "<p>Explore our features and let us know if you have any questions.</p>" +
                "<p>Best regards,<br>Team</p>";

        MailRequest request = MailRequest.builder()
                .from(mailFrom)
                .senderName(senderName)
                .to(user.getEmail())
                .subject(subject)
                .text(mailContent)
                .build();

        sendEmail(request);
    }

    @Override
    public void sendTokenForForgetPassword(User user, String resetToken) {
        String subject = "Password Reset Request";
        String mailContent = "<div style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                "<p style=\"font-size: 16px;\">Hi <strong>" + user.getUsername() + "</strong>,</p>" +
                "<p style=\"font-size: 14px;\">We received a request to reset your password. Use the following token to reset it:</p>" +
                "<p style=\"font-size: 18px; font-weight: bold; text-align: center; color: #28a745;\">" + resetToken + "</p>" +
                "<p style=\"font-size: 14px;\">Enter this token in the password reset form to proceed.</p>" +
                "<p style=\"font-size: 14px;\">If you did not request this, you can safely ignore this email.</p>" +
                "<p style=\"font-size: 14px;\">Thank you,<br><strong>Team</strong></p>" +
                "</div>";

        MailRequest request = MailRequest.builder()
                .from(mailFrom)
                .senderName(senderName)
                .to(user.getEmail())
                .subject(subject)
                .text(mailContent)
                .build();

        sendEmail(request);
    }

    @Override
    public void sendPaymentReceipt(User user, Payment payment, Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order)
                .orElseThrow(() -> new RuntimeException("Order items not found"));

        List<OrderItemResponse> orderItemResponses = orderItemMapper.toOrderItemResponseList(orderItems);
        OrderResponse response = orderMapper.toOrderResponse(order);
        BigDecimal totalPrice = order.getTotalAmount();

        String formattedAmount = totalPrice.setScale(2, RoundingMode.HALF_UP) + " " + payment.getCurrency();

        // Construct order items table rows dynamically
        StringBuilder orderItemsTable = new StringBuilder();
        for (OrderItemResponse item : orderItemResponses) {
            orderItemsTable.append("<tr>")
                    .append("<td style='padding: 8px; border: 1px solid #ddd;'>").append(item.productName()).append("</td>")
                    .append("<td style='padding: 8px; border: 1px solid #ddd;'>").append(item.quantity()).append("</td>")
                    .append("<td style='padding: 8px; border: 1px solid #ddd;'>$").append(item.priceAtPurchase()).append("</td>")
                    .append("</tr>");
        }

        String mailContent = "<div style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>"
                + "<p style='font-size: 18px;'>Hi " + user.getUsername() + ",</p>"
                + "<p>Thank you for your generous donation. Here are the details:</p>"
                + "<table style='border-collapse: collapse; width: 100%; margin-bottom: 20px;'>"
                + "  <tr>"
                + "    <td style='padding: 8px; border: 1px solid #ddd; font-weight: bold;'>Receipt ID:</td>"
                + "    <td style='padding: 8px; border: 1px solid #ddd;'>" + payment.getUuid() + "</td>"
                + "  </tr>"
                + "  <tr>"
                + "    <td style='padding: 8px; border: 1px solid #ddd; font-weight: bold;'>Amount:</td>"
                + "    <td style='padding: 8px; border: 1px solid #ddd;'>$" + formattedAmount + "</td>"
                + "  </tr>"
                + "  <tr>"
                + "    <td style='padding: 8px; border: 1px solid #ddd; font-weight: bold;'>Date:</td>"
                + "    <td style='padding: 8px; border: 1px solid #ddd;'>" + payment.getPaymentDate() + "</td>"
                + "  </tr>"
                + "</table>"
                + "<h3>Order Details:</h3>"
                + "<table style='border-collapse: collapse; width: 100%; margin-bottom: 20px;'>"
                + "<tr>"
                + "<th style='padding: 8px; border: 1px solid #ddd; background-color: #f2f2f2;'>Product</th>"
                + "<th style='padding: 8px; border: 1px solid #ddd; background-color: #f2f2f2;'>Quantity</th>"
                + "<th style='padding: 8px; border: 1px solid #ddd; background-color: #f2f2f2;'>Price</th>"
                + "</tr>"
                + orderItemsTable
                + "</table>"
                + "<p style='margin-top: 20px;'>We truly appreciate your support!</p>"
                + "<p style='margin-top: 20px;'>Best regards,<br>Team</p>"
                + "</div>";

        MailRequest request = MailRequest.builder()
                .from(mailFrom)
                .senderName(senderName)
                .to(user.getEmail())
                .subject("Payment Receipt - Thank You for Your Donation!")
                .text(mailContent)
                .build();

        sendEmail(request);
    }


}
