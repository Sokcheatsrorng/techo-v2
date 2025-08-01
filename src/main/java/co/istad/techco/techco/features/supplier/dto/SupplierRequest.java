package co.istad.techco.techco.features.supplier.dto;

import co.istad.techco.techco.features.address.dto.AddressRequest;

public record SupplierRequest(

    String name,

    String email,

    String phone,

    AddressRequest address

) {
}
