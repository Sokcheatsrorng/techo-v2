package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Brand;
import co.istad.techco.techco.features.brand.dto.BrandRequest;
import co.istad.techco.techco.features.brand.dto.BrandResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "products", ignore = true)
    Brand fromBrandRequest(BrandRequest brandRequest);

    BrandResponse toBrandResponse(Brand brand);
}
