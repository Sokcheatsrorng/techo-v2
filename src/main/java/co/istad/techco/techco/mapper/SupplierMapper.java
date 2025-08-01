package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Supplier;
import co.istad.techco.techco.features.supplier.dto.SupplierRequest;
import co.istad.techco.techco.features.supplier.dto.SupplierResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {

    @Mapping(target = "address", source = "address")
    Supplier fromSupplierRequest(SupplierRequest supplierRequest);

    SupplierResponse tpSupplierResponse(Supplier supplier);

}
