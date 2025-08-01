package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Address;
import co.istad.techco.techco.domain.Supplier;
import co.istad.techco.techco.features.address.dto.AddressRequest;
import co.istad.techco.techco.features.address.dto.AddressResponse;
import co.istad.techco.techco.features.supplier.dto.SupplierRequest;
import co.istad.techco.techco.features.supplier.dto.SupplierResponse;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-03T22:58:19+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class SupplierMapperImpl implements SupplierMapper {

    @Override
    public Supplier fromSupplierRequest(SupplierRequest supplierRequest) {
        if ( supplierRequest == null ) {
            return null;
        }

        Supplier supplier = new Supplier();

        supplier.setAddress( addressRequestToAddress( supplierRequest.address() ) );
        supplier.setName( supplierRequest.name() );
        supplier.setEmail( supplierRequest.email() );
        supplier.setPhone( supplierRequest.phone() );

        return supplier;
    }

    @Override
    public SupplierResponse tpSupplierResponse(Supplier supplier) {
        if ( supplier == null ) {
            return null;
        }

        String name = null;
        String uuid = null;
        String email = null;
        String phone = null;
        AddressResponse address = null;

        name = supplier.getName();
        uuid = supplier.getUuid();
        email = supplier.getEmail();
        phone = supplier.getPhone();
        address = addressToAddressResponse( supplier.getAddress() );

        SupplierResponse supplierResponse = new SupplierResponse( name, uuid, email, phone, address );

        return supplierResponse;
    }

    protected Address addressRequestToAddress(AddressRequest addressRequest) {
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

    protected AddressResponse addressToAddressResponse(Address address) {
        if ( address == null ) {
            return null;
        }

        String uuid = null;
        String addressLine1 = null;
        String addressLine2 = null;
        String road = null;
        String linkAddress = null;

        uuid = address.getUuid();
        addressLine1 = address.getAddressLine1();
        addressLine2 = address.getAddressLine2();
        road = address.getRoad();
        linkAddress = address.getLinkAddress();

        AddressResponse addressResponse = new AddressResponse( uuid, addressLine1, addressLine2, road, linkAddress );

        return addressResponse;
    }
}
