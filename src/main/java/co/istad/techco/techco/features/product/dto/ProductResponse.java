package co.istad.techco.techco.features.product.dto;

import co.istad.techco.techco.domain.ColorOption;
import co.istad.techco.techco.features.brand.dto.BrandResponse;
import co.istad.techco.techco.features.category.dto.CategoryResponse;
import co.istad.techco.techco.features.supplier.dto.SupplierResponse;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record ProductResponse(

        String uuid,

        String name,

        String description,

        ComputerSpecResponse computerSpec,

        Integer stockQuantity,

        BigDecimal priceOut,

        Double discount,

        List<ColorOption> color,

        String thumbnail,

        List<String> images,

        String filteredImage,

        String warranty, Boolean availability,

        CategoryResponse category,

        SupplierResponse supplier,

        BrandResponse brand

) {
}
