package co.istad.techco.techco.features.brand;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.features.brand.dto.BrandRequest;
import co.istad.techco.techco.features.brand.dto.BrandResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @PostMapping
    public void createBrand(@Valid @RequestBody BrandRequest request) {
        brandService.createBrand(request);
    }

    @GetMapping("/{uuid}")
    public BrandResponse getBrand(@PathVariable(name = "uuid") String uuid) {
        return brandService.getByUuid(uuid);
    }

    @GetMapping
    public Page<BrandResponse> getAllBrands(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "12") Integer size,
            @RequestParam(name = "name", required = false, defaultValue = "") String name
    ) {
        return brandService.getAll(page, size, name);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{uuid}")
    public BasedMessage deleteBrand(@PathVariable(name = "uuid") String uuid) {

        return brandService.deleteByUuid(uuid);

    }

    @PutMapping("/{uuid}")
    public BasedMessage updateBrand(@PathVariable(name = "uuid") String uuid, @Valid @RequestBody BrandRequest request) {

        return brandService.updateByUuid(uuid, request);

    }

}
