package co.istad.techco.techco.features.product;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.*;
import co.istad.techco.techco.features.brand.BrandRepository;
import co.istad.techco.techco.features.category.CategoryRepository;
import co.istad.techco.techco.features.product.dto.ProductCreateRequest;
import co.istad.techco.techco.features.product.dto.ProductResponse;
import co.istad.techco.techco.features.product.dto.ProductUpdateRequest;
import co.istad.techco.techco.features.stock.StockRepository;
import co.istad.techco.techco.features.supplier.SupplierRepository;
import co.istad.techco.techco.mapper.*;
import co.istad.techco.techco.utils.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final SupplierRepository supplierRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final BrandRepository brandRepository;
    private final ComputerSpecMapper computerSpecMapper;
    private final CategoryMapper categoryMapper;
    private final BrandMapper brandMapper;
    private final SupplierMapper supplierMapper;


    @Transactional
    @Override
    public void createProduct(ProductCreateRequest request) {
        Supplier supplier = supplierRepository.findByUuid(request.supplierUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));

        Category category = categoryRepository.findByUuid(request.categoryUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        Brand brand = brandRepository.findByUuid(request.brandUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));

        // Convert request to entity
        Product product = productMapper.fromProductCreateRequest(request);
        product.setCategory(category);
        product.setSupplier(supplier);
        product.setBrand(brand);
        product.setUuid(Utils.generateUuid());

        // Create ComputerSpec and associate it with Product
        ComputerSpec computerSpec = computerSpecMapper.fromComputerSpecRequest(request.computerSpec());
        computerSpec.setProduct(product);
        product.setComputerSpec(computerSpec);

        // Persist Product (which cascades ComputerSpec)
        productRepository.save(product);

        // Save stock entry
        Stock stock = new Stock();
        stock.setUuid(Utils.generateUuid());
        stock.setQuantityDelta(request.stockQuantity());
        stock.setReason(Stock.Reason.PURCHASED);
        stock.setProduct(product);

        stockRepository.save(stock);
    }

    @Override
    public ProductResponse getByUuid(String uuid, String color) {

        // Retrieve the product based on the UUID
        Product product = productRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product has not been found"
                ));

        List<ColorOption> colors = product.getColor();  // List of ColorOption (color and image pair)
        List<String> images = product.getImages();      // List of images associated with the product

        String selectedImage = null;

        // Only proceed if both colors and images are not null
        if (colors != null && images != null && !colors.isEmpty() && !images.isEmpty()) {
            // Find the color option matching the provided color
            for (int i = 0; i < colors.size(); i++) {
                if (colors.get(i).getColor().equalsIgnoreCase(color)) {
                    // If color matches, set the corresponding image
                    if (i < images.size()) {
                        selectedImage = images.get(i);
                    }
                    break;  // Stop the loop once we find a match
                }
            }
            }

        // Build the ProductResponse and return
        return ProductResponse.builder()
                .uuid(product.getUuid())
                .name(product.getName())
                .description(product.getDescription())
                .stockQuantity(product.getStockQuantity())
                .priceOut(product.getPriceOut())
                .discount(product.getDiscount())
                .color(product.getColor())  // Return the whole list of color options
                .thumbnail(product.getThumbnail())
                .images(product.getImages())  // Return the whole list of images
                .filteredImage(selectedImage)  // Return the filtered image based on the selected color
                .warranty(product.getWarranty())
                .availability(product.getAvailability())
                .category(categoryMapper.toCategoryResponse(product.getCategory()))
                .supplier(supplierMapper.tpSupplierResponse(product.getSupplier()))
                .brand(brandMapper.toBrandResponse(product.getBrand()))
                .build();
    }

    @Override
    public Page<ProductResponse> getAll(int page, int size, String name) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page can't be negative"
            );
        }

        if (size < 1)
        {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Size can't be negative"
            );
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        // ✅ Use Elasticsearch if name is provided for fast searching
        Page<Product> products;
        if (name != null) {
            products = productRepository.findByNameContainingIgnoreCase(name, pageRequest);
        } else {
            products = productRepository.findAll(pageRequest);
        }

        return products.map(productMapper::toProductResponse);
    }

    @Transactional
    @Override
    public void updateStockAvailability() {
        stockRepository.markUnavailableStocks();
    }


    @Override
    public Page<ProductResponse> getByCategoryUuid(int page, int size, String categoryUuid) {

        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be greater than zero");
        }

        if (size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size must be greater than zero");
        }

        // ✅ Get category from PostgreSQL
        Category category = categoryRepository.findByUuid(categoryUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        PageRequest pageRequest = PageRequest.of(page, size);

        // ✅ Use Elasticsearch for category-based search
        Page<Product> products = productRepository.findByCategory(category, pageRequest);

        return products.map(productMapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> getByBrand(int page, int size, String brandUuid) {

        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number must be greater than zero");
        }

        if (size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size must be greater than zero");
        }

        // ✅ Get category from PostgreSQL
        Brand brand = brandRepository.findByUuid(brandUuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));

        PageRequest pageRequest = PageRequest.of(page, size);

        // ✅ Use Elasticsearch for category-based search
        Page<Product> products = productRepository.findByBrand(brand, pageRequest);

        return products.map(productMapper::toProductResponse);
    }

    @Override
    public BasedMessage deleteByUuid(String uuid) {

        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // Find the product
        Product product = productRepository.findByUuid(uuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        );

        // Check if current user is the owner/creator of the product
        if (!product.getCreatedBy().equals(currentUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You are not authorized to delete this product");
        }

        productRepository.delete(product);

        return new BasedMessage("Product deleted successfully");

    }

    @Override
    public ProductResponse updateByUuid(String uuid, ProductUpdateRequest request) {

        // Fetch existing product
        Product product = productRepository.findByUuid(uuid).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
        );

        // Update basic fields
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPriceIn(request.priceIn());
        product.setPriceOut(request.priceOut());
        product.setDiscount(request.discount());
        product.setColor(request.color());
        product.setThumbnail(request.thumbnail());
        product.setWarranty(request.warranty());
        product.setAvailability(request.availability());
        product.setImages(request.images());
        product.setStockQuantity(request.stockQuantity());
        product.setUpdatedAt(LocalDateTime.now());

        // Update Category
        Category category = categoryRepository.findByUuid(request.categoryUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        product.setCategory(category);

        // Update Supplier
        Supplier supplier = supplierRepository.findByUuid(request.supplierUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
        product.setSupplier(supplier);

        // Update Brand
        Brand brand = brandRepository.findByUuid(request.brandUuid())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found"));
        product.setBrand(brand);

        // Save updated product
        productRepository.save(product);

        // Return updated ProductResponse
        return productMapper.toProductResponse(product);
    }

}
