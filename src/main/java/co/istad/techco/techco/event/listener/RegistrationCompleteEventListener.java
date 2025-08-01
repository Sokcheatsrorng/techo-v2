package co.istad.techco.techco.event.listener;

import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.event.RegistrationCompleteEvent;
import co.istad.techco.techco.features.mail.MailService;
import co.istad.techco.techco.features.mail.verificationtoken.VerificationToken;
import co.istad.techco.techco.features.user.UserService;
import co.istad.techco.techco.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {

    private final MailService mailService;
    private final UserService userService;

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        // 1. Get the newly registered user
        User theUser = event.getUser();

        // 2. Create a verification token for the user
        String verificationToken = Utils.generateDigitsToken();

        // 3. Save the verification token for the user
        userService.saveUserVerificationToken(theUser, verificationToken, VerificationToken.TokenType.EMAIL_VERIFICATION);

        // 4. Build the verification URL to be sent to the user
        String url = event.getApplicationUrl() + "/api/v1/users/verify-email?token=" + verificationToken;

        log.info("URL: {}", url);

        // 5. Send the email
        mailService.sendTokenForEmailVerification(theUser, verificationToken);

        log.info("Click the link to verify your registration: {}", url);
    }
}
