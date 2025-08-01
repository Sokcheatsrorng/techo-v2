package co.istad.techco.techco.features.category;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.Category;
import co.istad.techco.techco.features.category.dto.CategoryRequest;
import co.istad.techco.techco.features.category.dto.CategoryResponse;
import co.istad.techco.techco.mapper.CategoryMapper;
import co.istad.techco.techco.utils.Utils;
import jdk.jshell.execution.Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public void createCategory(CategoryRequest request) {

        if (categoryRepository.existsByName(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Category name already exists"
            );
        }

        Category category = categoryMapper.fromCategoryRequest(request);

        category.setUuid(Utils.generateUuid());

        categoryRepository.save(category);
    }

    @Override
    public CategoryResponse getByUuid(String uuid) {
        return categoryMapper.toCategoryResponse(
                categoryRepository.findByUuid(uuid)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Category has not been found"
                                )
                        )
        );
    }

    @Override
    public Page<CategoryResponse> getAll(int page, int size, String name) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page must be greater than 0"
            );
        }

        if (size < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Size must be greater than 0"
            );
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Category> categories;

        if (name != null) {

            categories = categoryRepository.findByNameContainingIgnoreCase(name, pageRequest);

        } else {
            categories = categoryRepository.findAll(pageRequest);
        }

        return categories.map(categoryMapper::toCategoryResponse);
    }


    @Override
    public BasedMessage deleteCategoryByUuid(String uuid) {

        Category category = categoryRepository.findByUuid(uuid).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category has not been found"
                )
        );

        categoryRepository.delete(category);

        return new BasedMessage("Category has been deleted.");

    }

    @Override
    public BasedMessage updateByUuid(String uuid, CategoryRequest request) {

        Category category = categoryRepository.findByUuid(uuid).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category has not been found"
                )
        );

        category.setName(request.name());
        category.setDescription(request.description());
        categoryRepository.save(category);

        return new BasedMessage("Category has been updated.");
    }
}
