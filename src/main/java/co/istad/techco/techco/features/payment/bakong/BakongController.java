package co.istad.techco.techco.features.payment.bakong;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.features.payment.bakong.dto.BakongPaymentRequest;
import co.istad.techco.techco.features.payment.qr.QrGenerator;
import co.istad.techco.techco.features.payment.qr.dto.QrRequest;
import co.istad.techco.techco.features.payment.qr.dto.QrResponse;
import jakarta.validation.Valid;
import kh.gov.nbc.bakong_khqr.model.IndividualInfo;
import kh.gov.nbc.bakong_khqr.model.KHQRData;
import kh.gov.nbc.bakong_khqr.model.KHQRResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/bakong-payment")
public class BakongController {

    private final BakongService bakongService;

    @PostMapping("/generate-individual-qr")
    public ResponseEntity<?> generateIndividualQR(@Valid @RequestBody BakongPaymentRequest request) {
        try {
            IndividualInfo individualInfo = bakongService.createIndividualInfo(request);
            KHQRResponse<KHQRData> response = bakongService.generateQrIndividual(individualInfo);

            if (response.getKHQRStatus().getCode() == 0) {
                individualInfo.setTerminalLabel(response.getData().getQr());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BasedMessage(response.getKHQRStatus().getMessage()));
            }
        } catch (Exception e) {
            log.error("Error generating individual QR", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BasedMessage("Internal Server Error"));
        }
    }

    @PostMapping("/generate-qr")
    public ResponseEntity<?> generateQRCode(@Valid @RequestBody QrRequest QrRequest) {
        try {
            // Generate QR Code as Base64
            String base64QR = QrGenerator.generateQRCode(QrRequest.qr(), null);

            // Build the response
            QrResponse response = new QrResponse(base64QR);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Handle exceptions gracefully
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BasedMessage("Failed to generate QR code: " + e.getMessage()));
        }
    }

}
