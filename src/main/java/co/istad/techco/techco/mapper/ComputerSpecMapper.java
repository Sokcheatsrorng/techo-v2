package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.ComputerSpec;
import co.istad.techco.techco.features.product.dto.ComputerSpecResponse;
import co.istad.techco.techco.features.product.spec.ComputerSpecRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ComputerSpecMapper {

    ComputerSpec fromComputerSpecRequest(ComputerSpecRequest request);

    ComputerSpecResponse toComputerSpecResponse(ComputerSpec computerSpec);

}
