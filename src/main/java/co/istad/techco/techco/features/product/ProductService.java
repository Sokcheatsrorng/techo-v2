package co.istad.techco.techco.features.product;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.features.product.dto.ProductCreateRequest;
import co.istad.techco.techco.features.product.dto.ProductResponse;
import co.istad.techco.techco.features.product.dto.ProductUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    void createProduct(@Valid ProductCreateRequest request);

    ProductResponse getByUuid(String uuid, String color);

    Page<ProductResponse> getAll(int page, int size, String name);

    void updateStockAvailability();

    Page<ProductResponse> getByCategoryUuid(int page, int size, String categoryUuid);

    Page<ProductResponse> getByBrand(int page, int size, String brandUuid);

    BasedMessage deleteByUuid(String uuid);

    ProductResponse updateByUuid(String uuid, ProductUpdateRequest request);
}
