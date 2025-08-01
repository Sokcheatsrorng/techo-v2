package co.istad.techco.techco.features.user.dto;

import co.istad.techco.techco.features.address.dto.AddressResponse;

import java.util.List;

public record UserResponse(

        String uuid,

        String username,

        String email,

        String phoneNumber,

        String profile,

        boolean isDeleted,

        boolean emailVerified,

        List<RoleResponse> roles,

        AddressResponse address

) {
}
