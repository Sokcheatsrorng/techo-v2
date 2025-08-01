package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Category;
import co.istad.techco.techco.features.category.dto.CategoryRequest;
import co.istad.techco.techco.features.category.dto.CategoryResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-03T22:58:19+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public Category fromCategoryRequest(CategoryRequest categoryRequest) {
        if ( categoryRequest == null ) {
            return null;
        }

        Category category = new Category();

        category.setName( categoryRequest.name() );
        category.setDescription( categoryRequest.description() );

        return category;
    }

    @Override
    public CategoryResponse toCategoryResponse(Category category) {
        if ( category == null ) {
            return null;
        }

        String uuid = null;
        String name = null;
        String description = null;

        uuid = category.getUuid();
        name = category.getName();
        description = category.getDescription();

        String media = null;

        CategoryResponse categoryResponse = new CategoryResponse( uuid, name, description, media );

        return categoryResponse;
    }
}
