package co.istad.techco.techco.features.auth.jwt_token;

import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.features.auth.dto.AuthResponse;
import org.springframework.security.core.Authentication;

/**
 * The TokenService interface defines a contract for managing the creation of authentication tokens.
 * It provides methods for generating access tokens, refresh tokens, and combined authentication responses
 * for user sessions.
 */
public interface TokenService {

    /**
     * Creates a combined authentication token response containing both access and refresh tokens.
     *
     * @param user the user for whom the tokens are being created
     * @param authentication the authentication object containing the user's credentials and authorities
     * @return an authentication response containing the generated access and refresh tokens
     */
    AuthResponse createToken(User user, Authentication authentication);

    /**
     * Creates a new access token for the authenticated user.
     *
     * @param authentication the authentication object containing the user's credentials and authorities
     * @return the generated access token as a string
     */
    String createAccessToken(Authentication authentication);

    /**
     * Creates a new refresh token for the authenticated user.
     *
     * @param authentication the authentication object containing the user's credentials and authorities
     * @return the generated refresh token as a string
     */
    String createRefreshToken(Authentication authentication);
}
