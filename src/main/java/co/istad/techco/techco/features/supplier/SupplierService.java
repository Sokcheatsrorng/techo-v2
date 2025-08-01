package co.istad.techco.techco.features.supplier;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.features.supplier.dto.SupplierRequest;
import co.istad.techco.techco.features.supplier.dto.SupplierResponse;
import org.springframework.data.domain.Page;

public interface SupplierService {
    void createSupplier(SupplierRequest request);

    Page<SupplierResponse> getAllSuppliers(int page, int size, String name);

    SupplierResponse getByUuid(String uuid);

    BasedMessage deleteByUuid(String uuid);

    BasedMessage updateByUuid(String uuid, SupplierRequest request);
}
