package co.istad.techco.techco.features.mail.verificationtoken;

import co.istad.techco.techco.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    Optional<VerificationToken> findByTokenAndType(String token, VerificationToken.TokenType type);

    Optional<VerificationToken> findByUser(User user);

}
