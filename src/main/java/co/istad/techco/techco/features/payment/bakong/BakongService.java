package co.istad.techco.techco.features.payment.bakong;

import co.istad.techco.techco.features.payment.bakong.dto.BakongPaymentRequest;
import kh.gov.nbc.bakong_khqr.model.IndividualInfo;
import kh.gov.nbc.bakong_khqr.model.KHQRData;
import kh.gov.nbc.bakong_khqr.model.KHQRResponse;

public interface BakongService {

    IndividualInfo createIndividualInfo(BakongPaymentRequest request);

    KHQRResponse<KHQRData> generateQrIndividual(IndividualInfo individualInfo);

}
