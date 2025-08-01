package co.istad.techco.techco.mapper;

import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.features.user.dto.RegistrationRequest;
import co.istad.techco.techco.features.user.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {AddressMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "roles", source = "roles")
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "isAccountVerified", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "deleted", ignore = true)
//    @Mapping(target = "emailmailVerified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "verificationTokens", ignore = true)
    @Mapping(target = "address", source = "address")
    User fromRegistrationRequest(RegistrationRequest registrationRequest);

//    User fromUserResponse(UserResponse userResponse);

}
