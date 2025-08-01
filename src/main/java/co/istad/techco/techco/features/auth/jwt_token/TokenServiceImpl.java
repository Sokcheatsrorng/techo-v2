package co.istad.techco.techco.features.auth.jwt_token;

import co.istad.techco.techco.domain.User;
import co.istad.techco.techco.features.auth.dto.AuthResponse;
import co.istad.techco.techco.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final JwtEncoder jwtEncoder;
    private final UserMapper userMapper;

    @Qualifier("refreshJwtEncoder")
    private JwtEncoder refreshJwtEncoder;

    @Qualifier("refreshJwtEncoder")
    @Autowired
    public void setRefreshJwtEncoder(JwtEncoder refreshJwtEncoder) {
        this.refreshJwtEncoder = refreshJwtEncoder;
    }

    @Override
    public AuthResponse createToken(User user, Authentication authentication) {
        final String TOKEN_TYPE = "Bearer";
        return new AuthResponse(
                TOKEN_TYPE,
                createAccessToken(authentication),
                createRefreshToken(authentication),
                userMapper.toUserResponse(user)
        );
    }

    @Override
    public String createAccessToken(Authentication authentication) {

        String scope = "";

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            scope = jwt.getClaimAsString("scope");
        } else {
            scope = authentication.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> !authority.startsWith("ROLE_"))
                    .collect(Collectors.joining(" "));
        }

        log.info("Scope: {}", scope);
        log.info("Name of authentication: {}", authentication.getName());

        Instant now = Instant.now();

        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .id(authentication.getName())
                .subject("Access Resource")
                .audience(List.of("WEB", "MOBILE"))
                .issuedAt(now)
                .expiresAt(now.plus(7, ChronoUnit.DAYS))
                .issuer(authentication.getName())
                .claim("scope", scope)
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwtClaimsSet)).getTokenValue();
    }

    @Override
    public String createRefreshToken(Authentication auth) {

        String scope = "";

        if (auth.getPrincipal() instanceof Jwt jwt) {
            scope = jwt.getClaimAsString("scope");
        } else {
            scope = auth.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(authority -> !authority.startsWith("ROLE_"))
                    .collect(Collectors.joining(" "));
        }

        Instant now = Instant.now();

        JwtClaimsSet refreshJwtClaimsSet = JwtClaimsSet.builder()
                .id(auth.getName())
                .subject("Refresh Resource")
                .audience(List.of("WEB", "MOBILE"))
                .issuedAt(now)
                .expiresAt(now.plus(7, ChronoUnit.DAYS))
                .issuer(auth.getName())
                .claim("scope", scope)
                .build();

        return refreshJwtEncoder.encode(JwtEncoderParameters.from(refreshJwtClaimsSet)).getTokenValue();
    }
}
