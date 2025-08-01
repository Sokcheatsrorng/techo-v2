package co.istad.techco.techco.features.supplier.dto;

import co.istad.techco.techco.features.address.dto.AddressResponse;

public record SupplierResponse(

        String name,

        String uuid,

        String email,

        String phone,

        AddressResponse address

) {
}
