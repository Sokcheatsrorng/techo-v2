package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Product;
import co.istad.techco.techco.features.product.ProductService;
import co.istad.techco.techco.features.product.dto.ProductCreateRequest;
import co.istad.techco.techco.features.product.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ComputerSpecMapper.class})
public interface ProductMapper {

    @Mapping(target = "category.uuid", source = "categoryUuid")
    @Mapping(target = "supplier.uuid", source = "supplierUuid")
    @Mapping(target = "computerSpec", source = "computerSpec")
    Product fromProductCreateRequest(ProductCreateRequest productCreateRequest);

    @Mapping(source = "computerSpec", target = "computerSpec")
    ProductResponse toProductResponse(Product product);

//    @Mapping(target = "category", source = "category")
//    @Mapping(target = "supplier", source = "supplier")
//    @Mapping(target = "brand", source = "brand")
//    ProductResponse toProductResponse(Product product);

}
