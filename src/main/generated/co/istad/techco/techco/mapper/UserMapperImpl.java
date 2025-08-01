package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.Address;
import co.istad.techco.techco.domain.Role;
import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.features.address.dto.AddressResponse;
import co.istad.techco.techco.features.user.dto.RegistrationRequest;
import co.istad.techco.techco.features.user.dto.RoleResponse;
import co.istad.techco.techco.features.user.dto.UserResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-03T22:58:19+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public UserResponse toUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        List<RoleResponse> roles = null;
        String uuid = null;
        String username = null;
        String email = null;
        String phoneNumber = null;
        String profile = null;
        Boolean isEmailVerified = null;
        AddressResponse address = null;

        roles = roleListToRoleResponseList( user.getRoles() );
        uuid = user.getUuid();
        username = user.getUsername();
        email = user.getEmail();
        phoneNumber = user.getPhoneNumber();
        profile = user.getProfile();
        isEmailVerified = user.getIsEmailVerified();
        address = addressToAddressResponse( user.getAddress() );

        boolean isDeleted = false;

        UserResponse userResponse = new UserResponse( uuid, username, email, phoneNumber, profile, isDeleted, isEmailVerified, roles, address );

        return userResponse;
    }

    @Override
    public User fromRegistrationRequest(RegistrationRequest registrationRequest) {
        if ( registrationRequest == null ) {
            return null;
        }

        User user = new User();

        user.setAddress( addressMapper.fromAddressRequest( registrationRequest.address() ) );
        user.setUsername( registrationRequest.username() );
        user.setEmail( registrationRequest.email() );
        user.setPassword( registrationRequest.password() );
        user.setPhoneNumber( registrationRequest.phoneNumber() );
        user.setProfile( registrationRequest.profile() );

        return user;
    }

    protected RoleResponse roleToRoleResponse(Role role) {
        if ( role == null ) {
            return null;
        }

        String name = null;

        name = role.getName();

        RoleResponse roleResponse = new RoleResponse( name );

        return roleResponse;
    }

    protected List<RoleResponse> roleListToRoleResponseList(List<Role> list) {
        if ( list == null ) {
            return null;
        }

        List<RoleResponse> list1 = new ArrayList<RoleResponse>( list.size() );
        for ( Role role : list ) {
            list1.add( roleToRoleResponse( role ) );
        }

        return list1;
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
