package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Brand;
import co.istad.techco.techco.features.brand.dto.BrandRequest;
import co.istad.techco.techco.features.brand.dto.BrandResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-03T22:58:19+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class BrandMapperImpl implements BrandMapper {

    @Override
    public Brand fromBrandRequest(BrandRequest brandRequest) {
        if ( brandRequest == null ) {
            return null;
        }

        Brand brand = new Brand();

        brand.setName( brandRequest.name() );
        brand.setDescription( brandRequest.description() );
        brand.setBrandLogo( brandRequest.brandLogo() );

        return brand;
    }

    @Override
    public BrandResponse toBrandResponse(Brand brand) {
        if ( brand == null ) {
            return null;
        }

        String name = null;
        String uuid = null;
        String description = null;
        String brandLogo = null;

        name = brand.getName();
        uuid = brand.getUuid();
        description = brand.getDescription();
        brandLogo = brand.getBrandLogo();

        BrandResponse brandResponse = new BrandResponse( name, uuid, description, brandLogo );

        return brandResponse;
    }
}
