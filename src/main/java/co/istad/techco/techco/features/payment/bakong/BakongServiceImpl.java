package co.istad.techco.techco.features.payment.bakong;

import co.istad.techco.techco.domain.Order;
import co.istad.techco.techco.domain.Payment;
import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.features.order.OrderRepository;
import co.istad.techco.techco.features.payment.PaymentRepository;
import co.istad.techco.techco.features.payment.bakong.dto.BakongPaymentRequest;
import co.istad.techco.techco.features.user.UserRepository;
import co.istad.techco.techco.utils.Utils;
import kh.gov.nbc.bakong_khqr.BakongKHQR;
import kh.gov.nbc.bakong_khqr.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BakongServiceImpl implements BakongService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Value("${payment.bakong-account-id}")
    private String bakongAccountId;

    @Value("${payment.account-number}")
    private String bakongAccountNumber;

    @Value("${payment.acquiring-bank}")
    private String bakongAcquiringBank;

    @Value("${payment.md5-verification-url}")
    private String md5VerifyUrl;

    @Override
    public IndividualInfo createIndividualInfo(BakongPaymentRequest request) {

        User user = userRepository.findByUuid(request.userUuid())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User has not been found"
                ));

        Order order = orderRepository.findByUserAndUuid(user, request.orderUuid())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order has not been found"
                ));

        if (order.getOrderItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order has no items");
        }

        if (order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order total must be greater than zero");
        }

        IndividualInfo individualInfo = new IndividualInfo();

        // Populate IndividualInfo using Order details
        individualInfo.setAccountInformation(bakongAccountNumber);
        individualInfo.setBakongAccountId(bakongAccountId);
        individualInfo.setAcquiringBank(bakongAcquiringBank);
        individualInfo.setCurrency(KHQRCurrency.valueOf(request.currency()));
        individualInfo.setAmount(order.getTotalAmount().doubleValue());
        individualInfo.setMerchantName("iSHOP");
        individualInfo.setMerchantCity(request.city());

        return individualInfo;
    }


    @Override
    public KHQRResponse<KHQRData> generateQrIndividual(IndividualInfo individualInfo) {
        // Generate KHQR for Individual
        KHQRResponse<KHQRData> response = BakongKHQR.generateIndividual(individualInfo);

        if (response.getKHQRStatus().getCode() == 0) {

            verifyAndSavePayment(response.getData().getQr());

            System.out.println("QR Data: " + response.getData().getQr());
            System.out.println("MD5 Hash: " + response.getData().getMd5());



        } else {
            System.err.println("Error: " + response.getKHQRStatus().getMessage());
        }
        return response;
    }

    public void verifyAndSavePayment(String qrCode) {
        try {
            // Verify KHQR Code
            KHQRResponse<CRCValidation> response = BakongKHQR.verify(qrCode);

            if (response.getKHQRStatus().getCode() == 0 && response.getData().isValid()) {
                System.out.println("QR Code Valid and Payment Completed");

            } else {
                System.err.println("Payment Verification Failed: " + response.getKHQRStatus().getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Payment verification failed");
        }
    }
}
