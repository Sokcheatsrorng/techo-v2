package co.istad.techco.techco.features.user;

import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.features.mail.verificationtoken.VerificationToken;
import co.istad.techco.techco.features.user.dto.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse signUp(RegistrationRequest registrationRequest);

    void saveUserVerificationToken(User theUser, String verificationToken, VerificationToken.TokenType tokenType);

    String validateVerificationToken(String token);

    String validateForgetPasswordToken(String token);

    void forgetPassword(String email);

    void setNewPassword(String token,NewPasswordRequest request);

    Page<UserResponse> getAllUser(int page, int size, String username);

    UserResponse getUserInfo(String email);

    UserResponse getUserByUuid(String uuid);

    void setProfileImage(String uuid, MultipartFile file);

    void deleteByUuid(String uuid);

    void updateUserByUuid(String uuid, UserUpdateRequest request);

    void deleteUser(String uuid);

    void resentEmailVerification(ResendEmailRequestBody requestBody);

    void resendForgetPasswordToken(ResendForgetPasswordToken passwordToken);

    void updatePasswordByUuid(String uuid, PasswordUpdateRequest request);
}
