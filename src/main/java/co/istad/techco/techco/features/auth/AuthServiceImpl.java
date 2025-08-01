package co.istad.techco.techco.features.auth;


import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.features.auth.dto.AuthResponse;
import co.istad.techco.techco.features.auth.dto.LoginRequest;
import co.istad.techco.techco.features.auth.dto.RefreshTokenRequest;
import co.istad.techco.techco.features.auth.jwt_token.TokenService;
import co.istad.techco.techco.features.user.UserRepository;
import co.istad.techco.techco.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final TokenService tokenService;
    private final UserRepository userRepository;


    @Override
    public AuthResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User with email: " + loginRequest.email() + " not found"
                        )
                );

        if (!user.isEmailVerified()) {
            if (user.getCreatedAt().isBefore(LocalDateTime.now().minusHours(24))) {
                userRepository.delete(user);
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "User has not been verified and has been deleted"
                );
            } else {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "User has not been verified yet"
                );
            }
        }

        Authentication auth = new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.password()
        );

        auth = daoAuthenticationProvider.authenticate(auth);

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        userRepository.save(user);

        return tokenService.createToken(userDetails.getUser(), auth);
    }

    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {
        Authentication auth = new BearerTokenAuthenticationToken(request.refreshToken());

        auth = jwtAuthenticationProvider.authenticate(auth);

        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized"
            );
        }

        Jwt jwt = jwtAuth.getToken();

        // Extract user identifier (e.g., email) from the JWT claims
        String email = jwt.getClaimAsString("iss"); // Assuming 'sub' contains the user's email

        if (email == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token"
            );
        }

        log.info("Email: " + email);

        // Retrieve user from the database
        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User has not been found"
                        )
                );

        return tokenService.createToken(user, auth);
    }

}
