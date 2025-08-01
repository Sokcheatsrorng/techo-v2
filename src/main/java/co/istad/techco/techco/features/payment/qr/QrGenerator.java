package co.istad.techco.techco.features.payment.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

public class QrGenerator {

    public static String generateQRCode(String data, String filePath) throws WriterException, IOException {
        // Set QR Code parameters
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // Generate the BitMatrix
        BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300, hints);

        // Convert BitMatrix to BufferedImage
        BufferedImage qrImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {
                qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }

        // Save to file (optional)
        if (filePath != null) {
            File outputFile = new File(filePath);
            ImageIO.write(qrImage, "PNG", outputFile);
        }

        // Convert image to Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        byte[] qrCodeBytes = baos.toByteArray();

        return Base64.getEncoder().encodeToString(qrCodeBytes);
    }
}
