package co.istad.techco.techco.features.brand;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.features.brand.dto.BrandRequest;
import co.istad.techco.techco.features.brand.dto.BrandResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface BrandService {

    void createBrand(@Valid BrandRequest request);

    BrandResponse getByUuid(String uuid);

    Page<BrandResponse> getAll(int page, int size, String name);

    BasedMessage deleteByUuid(String uuid);

    BasedMessage updateByUuid(String uuid, @Valid BrandRequest request);

}
