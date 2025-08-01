package co.istad.techco.techco.features.product;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.features.product.dto.ProductCreateRequest;
import co.istad.techco.techco.features.product.dto.ProductResponse;
import co.istad.techco.techco.features.product.dto.ProductUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ✅ CREATE PRODUCT
    @PostMapping
    public ResponseEntity<BasedMessage> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        log.info("Creating product: {}", request);
        productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BasedMessage("Product created successfully"));
    }

    // ✅ UPDATE PRODUCT
    @PutMapping("/{uuid}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("uuid") String uuid,
            @Valid @RequestBody ProductUpdateRequest request) {

        log.info("Updating product with UUID: {}", uuid);
        ProductResponse updatedProduct = productService.updateByUuid(uuid, request);
        return ResponseEntity.ok(updatedProduct);
    }

    // ✅ GET PRODUCT BY UUID
    @GetMapping("/{uuid}")
    public ResponseEntity<ProductResponse> getByUuid(@PathVariable("uuid") String uuid,
                                                     @RequestParam(name = "color", required = false, defaultValue = "") String color) {
        log.info("Fetching product by UUID: {}", uuid);
        return ResponseEntity.ok(productService.getByUuid(uuid, color));
    }

    // ✅ GET PRODUCTS BY CATEGORY (WITH PAGINATION)
    @GetMapping("/get-by-category/{categoryUuid}")
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "12") Integer size,
            @PathVariable("categoryUuid") String categoryUuid) {

        log.info("Fetching products by category UUID: {} (Page: {}, Size: {})", categoryUuid, page, size);
        return ResponseEntity.ok(productService.getByCategoryUuid(page, size, categoryUuid));
    }

    // ✅ GET PRODUCTS BY BRAND (WITH PAGINATION)
    @GetMapping("/get-by-brand/{brandUuid}")
    public ResponseEntity<Page<ProductResponse>> getProductsByBrand(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "12") Integer size,
            @PathVariable("brandUuid") String brandUuid) {

        log.info("Fetching products by brand UUID: {} (Page: {}, Size: {})", brandUuid, page, size);
        return ResponseEntity.ok(productService.getByBrand(page, size, brandUuid));
    }

    // ✅ GET ALL PRODUCTS (WITH PAGINATION & FILTER BY NAME)
    @GetMapping
    public Page<ProductResponse> getAll(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "12") Integer size,
            @RequestParam(name = "name", required = false, defaultValue = "") String name) {

        log.info("Fetching all products (Page: {}, Size: {}, Name Filter: '{}')", page, size, name);
        return productService.getAll(page, size, name);
    }

    // ✅ DELETE PRODUCT BY UUID
    @DeleteMapping("/{uuid}")
    public ResponseEntity<BasedMessage> deleteByUuid(@PathVariable("uuid") String uuid) {
        log.info("Deleting product with UUID: {}", uuid);
        productService.deleteByUuid(uuid);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new BasedMessage("Product deleted successfully"));
    }

}
