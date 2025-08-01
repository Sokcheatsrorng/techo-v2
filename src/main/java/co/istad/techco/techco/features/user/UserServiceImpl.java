package co.istad.techco.techco.features.user;

import co.istad.techco.techco.domain.Address;
import co.istad.techco.techco.domain.Role;
import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.features.address.AddressRepository;
import co.istad.techco.techco.features.mail.MailService;
import co.istad.techco.techco.features.mail.verificationtoken.VerificationToken;
import co.istad.techco.techco.features.mail.verificationtoken.VerificationTokenRepository;
import co.istad.techco.techco.features.media.MediaService;
import co.istad.techco.techco.features.media.dto.MediaResponse;
import co.istad.techco.techco.features.roleauthority.RoleRepository;
import co.istad.techco.techco.features.user.dto.*;
import co.istad.techco.techco.mapper.AddressMapper;
import co.istad.techco.techco.mapper.UserMapper;
import co.istad.techco.techco.utils.Utils;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final AddressMapper addressMapper;
    private final MediaService mediaService;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public UserResponse signUp(RegistrationRequest registrationRequest) {

        Address address = addressMapper.fromAddressRequest(registrationRequest.address());
        address.setUuid(Utils.generateUuid());

        if(!registrationRequest.password().equals(registrationRequest.confirmPassword())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Confirm Password is not matched"
                );
        }

        User user = userMapper.fromRegistrationRequest(registrationRequest);

        if (userRepository.existsByUsernameOrEmail(registrationRequest.username(), registrationRequest.email())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email or username already exists"
            );
        }

        user.setEmail(registrationRequest.email());
        user.setPassword(passwordEncoder.encode(registrationRequest.password()));
        user.setUsername(registrationRequest.username());
        user.setProfile(registrationRequest.profile());
        user.setEmailVerified(false);
        user.setUuid(Utils.generateUuid());

        assignRolesToUser(user);
        initializeUserDefaults(user);

        user.setAddress(address);
        address.setUser(user);

        userRepository.save(user);

        address.setUser(user);
        addressRepository.save(address);

        return userMapper.toUserResponse(user);
    }

    private void assignRolesToUser(User user) {
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName("USER").orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role has not been found"
                )
        ));
        user.setRoles(roles);
    }
    private void initializeUserDefaults(User user) {
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setDeleted(false);
    }

    @Transactional
    @Override
    public void saveUserVerificationToken(User theUser, String token, VerificationToken.TokenType type) {

        VerificationToken verificationToken = new VerificationToken(token, VerificationToken.TokenType.EMAIL_VERIFICATION, theUser);

        verificationToken.setUser(theUser);

        verificationTokenRepository.save(verificationToken);

    }

    @Override
    public String validateVerificationToken(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndType(token, VerificationToken.TokenType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token"));

        return checkTokenExpiration(token, verificationToken);
    }

    @NotNull
    private String checkTokenExpiration(String token, VerificationToken verificationToken) {
        if (verificationToken.isExpired()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expired verification token");
        }

        if (!verificationToken.getToken().equals(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token");
        }

        verificationToken.setIsUsed(true);
        verificationTokenRepository.save(verificationToken);

        return "valid";
    }

    @Override
    public String validateForgetPasswordToken(String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndType(token, VerificationToken.TokenType.FORGET_PASSWORD)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token"));

        return checkTokenExpiration(token, verificationToken);
    }

    @Override
    public void forgetPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User with email: " + email + " has not been found!"
                        )
                );

        String forgetPasswordToken = Utils.generateDigitsToken();

        VerificationToken verificationToken = new VerificationToken(forgetPasswordToken, VerificationToken.TokenType.FORGET_PASSWORD, user);

        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);

        mailService.sendTokenForForgetPassword(user, verificationToken.getToken());

    }

    @Override
    public void setNewPassword(String token, NewPasswordRequest request) {
        String validatedToken = validateForgetPasswordToken(token);

        if (validatedToken.equals("valid")) {

            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(
                            () -> new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "User with email: " + request.email() + " has not been found!"
                            )
                    );

            if (!request.newPassword().equals(request.confirmPassword())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Passwords do not match!"
                );
            }

            user.setPassword(passwordEncoder.encode(request.newPassword()));
            userRepository.save(user);

        }
    }

    @Override
    public Page<UserResponse> getAllUser(int page, int size, String username) {

        if (page < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Page number must be greater than 0"
            );
        }

        if (size < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Size must be greater than 1"
            );
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<User> users = (username != null && !username.isEmpty())
                ? userRepository.findUsersByUsernameContaining(username, pageRequest)
                : userRepository.findAll(pageRequest);

        return users.map(userMapper::toUserResponse);

    }

    @Override
    public UserResponse getUserInfo(String email) {
        return userMapper.toUserResponse(
                userRepository.findByEmail(email)
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with email: " + email + " not found!")
                        )
        );
    }

    @Override
    public UserResponse getUserByUuid(String uuid) {
        return userMapper.toUserResponse(
                userRepository.findByUuid(uuid)
                        .orElseThrow(
                                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with uuid: " + uuid + " not found!")
                        )
        );
    }

    @Override
    public void setProfileImage(String uuid, MultipartFile file) {

        User user = userRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User has not been found!"
                        )
                );

        MediaResponse response =  mediaService.uploadSingle(file, "");

        user.setProfile(response.uri());

    }

    @Transactional
    @Override
    public void deleteByUuid(String uuid) {

        if (!userRepository.existsByUuid(uuid))
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteByUuid(uuid);

    }

    @Transactional
    @Override
    public void updateUserByUuid(String uuid, UserUpdateRequest request) {

        User user = userRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User with uuid: " + uuid + " not found!"
                        )
                );

        Address address = user.getAddress();
        address.setAddressLine1(request.address().addressLine1());
        address.setAddressLine2(request.address().addressLine2());
        address.setLinkAddress(request.address().linkAddress());
        address.setRoad(request.address().road());

        user.setAddress(address);
        user.setUsername(request.username());
        user.setPhoneNumber(request.phoneNumber());
        user.setProfile(request.profile());

        userRepository.save(user);
        addressRepository.save(address);

    }

    @Override
    public void deleteUser(String uuid) {

        User user = userRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User has not been found!"
                        )
                );

        userRepository.delete(user);

    }

    @Transactional
    @Override
    public void resentEmailVerification(ResendEmailRequestBody resendEmailRequestBody) {

        User user = userRepository.findByEmail(resendEmailRequestBody.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User has not been found!"));

        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndType(resendEmailRequestBody.oldToken(), VerificationToken.TokenType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token has not been found!"));

        if (verificationToken.isExpired()) {
            String newToken = Utils.generateDigitsToken();

            // Ensure user is assigned to the new token
            VerificationToken newVerificationToken = new VerificationToken(newToken, VerificationToken.TokenType.EMAIL_VERIFICATION, user);
            newVerificationToken.setUser(user); // Set user

            verificationTokenRepository.save(newVerificationToken); // Save new token

            mailService.sendTokenForEmailVerification(user, newVerificationToken.getToken());
        }
    }

    @Transactional
    @Override
    public void resendForgetPasswordToken(ResendForgetPasswordToken passwordToken) {

        User user = userRepository.findByEmail(passwordToken.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User has not been found!"));

        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndType(passwordToken.oldToken(), VerificationToken.TokenType.FORGET_PASSWORD)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token has not been found!"));

        if (verificationToken.isExpired()) {
            String newToken = Utils.generateDigitsToken();

            // Ensure user is assigned to the new token
            VerificationToken newVerificationToken = new VerificationToken(newToken, VerificationToken.TokenType.FORGET_PASSWORD, user);
            newVerificationToken.setUser(user); // Set user

            verificationTokenRepository.save(newVerificationToken); // Save new token

            mailService.sendTokenForForgetPassword(user, newVerificationToken.getToken());
        }
    }

    @Transactional
    @Override
    public void updatePasswordByUuid(String uuid, PasswordUpdateRequest request) {

        User user = userRepository.findByUuid(uuid)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User has not been found"
                        )
                );

        if (!(passwordEncoder.matches(request.oldPassword(), user.getPassword()))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Old password is incorrect"
            );
        }

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "New password and confirm password is not match"
            );
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);

    }

}
