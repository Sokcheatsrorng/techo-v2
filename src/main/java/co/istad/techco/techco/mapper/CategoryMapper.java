package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Category;
import co.istad.techco.techco.features.category.dto.CategoryRequest;
import co.istad.techco.techco.features.category.dto.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    Category fromCategoryRequest(CategoryRequest categoryRequest);

    CategoryResponse toCategoryResponse(Category category);

}
