package co.istad.techco.techco.features.user;

import co.istad.techco.techco.base.BasedMessage;
import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.event.RegistrationCompleteEvent;
import co.istad.techco.techco.features.mail.MailService;
import co.istad.techco.techco.features.mail.verificationtoken.VerificationToken;
import co.istad.techco.techco.features.mail.verificationtoken.VerificationTokenRepository;
import co.istad.techco.techco.features.user.dto.*;
import co.istad.techco.techco.mapper.UserMapper;
import co.istad.techco.techco.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final MailService mailService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    private final ApplicationEventPublisher eventPublisher;

    @PostMapping("/user-signup")
    public BasedMessage signup(@Valid @RequestBody RegistrationRequest request,
                               @RequestParam(required = false, defaultValue = "false") boolean emailVerified,
                               HttpServletRequest servletRequest) {

        UserResponse userResponse = userService.signUp(request);
        if (userResponse == null || userResponse.username() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "User signup failed");
        }

        User user = userRepository.findByUsername(userResponse.username())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found after signup"));

        log.info("User email: {}", user.getEmail());

        if (Boolean.FALSE.equals(emailVerified)) {
            publishRegistrationEvent(user, servletRequest);
            return BasedMessage.builder()
                    .message("User has been registered. Please verify your email address")
                    .build();
        }

        if (Boolean.TRUE.equals(emailVerified)) {
            user.setEmailVerified(true);

            userRepository.save(user);
        }

        return BasedMessage.builder()
                .message("You have registered successfully")
                .build();
    }

    private void publishRegistrationEvent(User user, HttpServletRequest servletRequest) {
        try {
            eventPublisher.publishEvent(new RegistrationCompleteEvent(user, Utils.getApplicationUrl(servletRequest)));
        } catch (Exception e) {
            log.error("Failed to publish registration event", e);
        }
    }

    @PostMapping("/verify-email")
    public RegistrationResponse verifyEmail(@RequestParam(name = "token") String token) {

        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndType(token, VerificationToken.TokenType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Token has not been found"));

        User user = verificationToken.getUser();

        String validatedToken = userService.validateVerificationToken(verificationToken.getToken());

        if (validatedToken.equals("valid")) {
            user.setEmailVerified(true);
            userRepository.save(user);

            mailService.welcomeMessage(user);

            return RegistrationResponse.builder()
                    .message("Registration successfully")
                    .user(userMapper.toUserResponse(user))
                    .code(200)
                    .status(true)
                    .timeStamp(LocalDateTime.now())
                    .data(user.getEmail())
                    .token(verificationToken.getToken())
                    .build();
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Token has not been found"
        );
    }

    @PostMapping("/resend-email-verification")
    public BasedMessage resendEmailVerification(@Valid @RequestBody ResendEmailRequestBody resendEmailRequestBody) {

        userService.resentEmailVerification(resendEmailRequestBody);

        return new BasedMessage("Token has been resent");
    }

    @PostMapping("/resend-password-reset-token")
    public BasedMessage resendForgetPasswordToken(@Valid @RequestBody ResendForgetPasswordToken passwordToken) {

        userService.resendForgetPasswordToken(passwordToken);

        return new BasedMessage("Token has been resent");

    }

    @PostMapping("/forgot-password")
    public BasedMessage forgetPassword(@Valid @RequestBody ForgetPasswordRequest request) {
        userService.forgetPassword(request.email());

        return BasedMessage.builder()
                .message("Password reset link has been sent to your email.")
                .build();

    }

    @PutMapping("/set-new-password")
    public BasedMessage setNewPassword(@RequestParam(name = "token") String token,
                                       @Valid @RequestBody NewPasswordRequest request) {

        userService.setNewPassword(token, request);

        return new BasedMessage("Password has been changed");

    }

    @PreAuthorize("hasAuthority('SCOPE_admin:read')")
    @GetMapping
    public Page<UserResponse> getAllUsers(
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "12") Integer size,
            @RequestParam(name = "username", required = false, defaultValue = "") String username) {

        return userService.getAllUser(page, size, username);

    }

    @GetMapping("/me")
    public UserResponse getCurrentUser(Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaim("iss");

        return userService.getUserInfo(email);

    }

    @GetMapping("/{uuid}")
    public UserResponse getUserByUuid(@PathVariable String uuid) {
        return userService.getUserByUuid(uuid);
    }

    @PutMapping("/upload-image/{uuid}")
    public BasedMessage setProfileImage(@PathVariable(name = "uuid") String uuid,
                                        @Valid @RequestBody MultipartFile file) {

        userService.setProfileImage(uuid, file);

        return new BasedMessage("Profile image has been updated");

    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{uuid}")
    public BasedMessage deleteByUuid(@PathVariable String uuid) {

        userService.deleteUser(uuid);

        return new BasedMessage("User has been deleted");

    }

    @PutMapping("/{uuid}")
    public BasedMessage updateUserByUuid(@PathVariable(name = "uuid") String uuid,
                                         @Valid @RequestBody UserUpdateRequest request) {


        userService.updateUserByUuid(uuid, request);

        return new BasedMessage("User has been updated");

    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/delete-user/{uuid}")
    public BasedMessage deleteUserByUuid(@PathVariable(name = "uuid") String uuid) {

        userService.deleteByUuid(uuid);

        return new BasedMessage("User has been deleted");

    }

    @PutMapping("/update-password/{uuid}")
    public BasedMessage updateNewPassword(@PathVariable(name = "uuid") String uuid,
                                          @Valid @RequestBody PasswordUpdateRequest request) {

        userService.updatePasswordByUuid(uuid, request);

        return new BasedMessage("Password has been updated");

    }

}
