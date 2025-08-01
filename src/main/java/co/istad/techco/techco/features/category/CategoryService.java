package co.istad.techco.techco.features.category;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.features.category.dto.CategoryRequest;
import co.istad.techco.techco.features.category.dto.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface CategoryService {

    void createCategory(@Valid CategoryRequest request);

    CategoryResponse getByUuid(String uuid);

    Page<CategoryResponse> getAll(int page, int size, String name);

    BasedMessage deleteCategoryByUuid(String uuid);

    BasedMessage updateByUuid(String uuid, CategoryRequest request);
}
