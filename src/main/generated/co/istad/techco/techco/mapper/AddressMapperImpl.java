package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Address;
import co.istad.techco.techco.features.address.dto.AddressRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-03T22:58:19+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class AddressMapperImpl implements AddressMapper {

    @Override
    public Address fromAddressRequest(AddressRequest addressRequest) {
        if ( addressRequest == null ) {
            return null;
        }

        Address address = new Address();

        address.setAddressLine1( addressRequest.addressLine1() );
        address.setAddressLine2( addressRequest.addressLine2() );
        address.setRoad( addressRequest.road() );
        address.setLinkAddress( addressRequest.linkAddress() );

        return address;
    }
}
