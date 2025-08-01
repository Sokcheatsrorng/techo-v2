package co.istad.techco.techco.features.category;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.features.category.dto.CategoryRequest;
import co.istad.techco.techco.features.category.dto.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public void createCategory(@Valid @RequestBody CategoryRequest request) {
        categoryService.createCategory(request);
    }

    @GetMapping("/{uuid}")
    public CategoryResponse getByUuid(@PathVariable(name = "uuid") String uuid) {

        return categoryService.getByUuid(uuid);

    }

    @GetMapping
    public Page<CategoryResponse> getAllCategories(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "12") Integer size,
            @RequestParam(name = "name", required = false, defaultValue = "") String name
    ) {
        return categoryService.getAll(page, size, name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{uuid}")
    public BasedMessage deleteByUuid(@PathVariable(name = "uuid") String uuid) {

        return categoryService.deleteCategoryByUuid(uuid);

    }

    @PutMapping("/{uuid}")
    public BasedMessage updateByUuid(@PathVariable(name = "uuid") String uuid, @Valid @RequestBody CategoryRequest request) {

        return categoryService.updateByUuid(uuid, request);

    }


}
