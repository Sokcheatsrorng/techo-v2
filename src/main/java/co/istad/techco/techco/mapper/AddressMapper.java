package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Address;
import co.istad.techco.techco.features.address.dto.AddressRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {

    Address fromAddressRequest(AddressRequest addressRequest);

}
