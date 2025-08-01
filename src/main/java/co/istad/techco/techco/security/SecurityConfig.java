package co.istad.techco.techco.security;

import co.istad.techco.techco.utils.KeyUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.UUID;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final KeyUtil keyUtil;

    @Bean
    JwtAuthenticationProvider jwtAuthenticationProvider(@Qualifier("refreshJwtDecoder") JwtDecoder refreshJwtDecoder) {
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(refreshJwtDecoder);
        return provider;
    }

    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);

        return provider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
//                .securityMatcher("/**")
                .cors(cor -> cor.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(request -> {
                    // Authentication & User Management
                    request
                            .requestMatchers(HttpMethod.GET, "media/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "api/v1/medias/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "api/v1/users/user-signup").permitAll()
                            .requestMatchers(HttpMethod.POST, "api/v1/users/verify-email").permitAll()
                            .requestMatchers(HttpMethod.POST, "api/v1/users/resend-email-verification").permitAll()
                            .requestMatchers(HttpMethod.POST, "api/v1/users/resend-password-reset-token").permitAll()
                            .requestMatchers(HttpMethod.POST, "api/v1/users/forgot-password").permitAll()
                            .requestMatchers(HttpMethod.PUT, "api/v1/users/set-new-password").permitAll()
//                            .requestMatchers(HttpMethod.POST, "api/v1/users/**").hasAuthority("SCOPE_admin:write")
                            .requestMatchers(HttpMethod.PUT, "api/v1/users/**").hasAnyAuthority("SCOPE_admin:write", "SCOPE_user:write")
                            .requestMatchers(HttpMethod.DELETE, "api/v1/users/**").hasAuthority("SCOPE_admin:write")
                            .requestMatchers(HttpMethod.GET, "api/v1/users/me").hasAnyAuthority("SCOPE_user:read", "SCOPE_admin:read")
                            .requestMatchers(HttpMethod.GET, "api/v1/users").hasAuthority("SCOPE_admin:read")

                            // Authentication
                            .requestMatchers(HttpMethod.POST, "api/v1/auth/**").permitAll()

                            // Categories
                            .requestMatchers(HttpMethod.GET, "api/v1/categories/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "api/v1/categories/**").hasAuthority("SCOPE_admin:write")
                            .requestMatchers(HttpMethod.PUT, "api/v1/categories/**").hasAuthority("SCOPE_admin:write")
                            .requestMatchers(HttpMethod.DELETE, "api/v1/categories/**").hasAuthority("SCOPE_admin:write")

                            // Products (public)
                            .requestMatchers(HttpMethod.GET, "api/v1/products/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "api/v1/products/**").hasAuthority("SCOPE_admin:write")

                            // Brands, Suppliers (public)
                            .requestMatchers("api/v1/brands/**").permitAll()
                            .requestMatchers("api/v1/suppliers/**").permitAll()

                            // Cart (User & Admin)
                            .requestMatchers(HttpMethod.GET, "api/v1/carts/get-by-user/**").hasAnyAuthority("SCOPE_user:read", "SCOPE_admin:read")
                            .requestMatchers(HttpMethod.GET, "api/v1/carts/**").hasAuthority("SCOPE_admin:read")
                            .requestMatchers(HttpMethod.POST, "api/v1/carts/add-to-cart").hasAuthority("SCOPE_user:write")
                            .requestMatchers(HttpMethod.PUT, "api/v1/carts/add-quantity/**").hasAuthority("SCOPE_user:write")
                            .requestMatchers(HttpMethod.PUT, "api/v1/carts/remove-quantity/**").hasAuthority("SCOPE_user:write")
                            .requestMatchers(HttpMethod.DELETE, "api/v1/carts/**").hasAuthority("SCOPE_user:write")

                            // Payments (public)
                            .requestMatchers("api/v1/bakong-payment/**").permitAll()
                            .requestMatchers("api/v1/qr/**").permitAll()
                            .requestMatchers("api/v1/medias/**").permitAll()
                            .requestMatchers("api/v1/orders/**").hasAnyAuthority("SCOPE_user:read", "SCOPE_admin:read")
                            .requestMatchers("api/v1/payments/**").hasAnyAuthority("SCOPE_user:read", "SCOPE_admin:read")

                            // Swagger & API Docs (public)
                            .requestMatchers(
                                    "/techco-api-docs/**",
                                    "/techco-api-ui.html",
                                    "/swagger-ui/**",
                                    "/v3/api-docs/**"
                            ).permitAll()

                            // Any other requests need authentication
                            .anyRequest().authenticated();
                });

        // Security mechanism
        http.oauth2ResourceServer(jwt -> jwt.jwt(Customizer.withDefaults()));

        // Disable CSRF
        http.csrf(AbstractHttpConfigurer::disable);

        // Stateless Session
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Primary
    @Bean
    JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = new RSAKey.Builder(keyUtil.getAccessTokenPublicKey())
                .privateKey(keyUtil.getAccessTokenPrivateKey())
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector
                .select(jwkSet);
    }

    @Primary
    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Primary
    @Bean
    JwtDecoder jwtDecoder() throws JOSEException {
        return NimbusJwtDecoder
                .withPublicKey(keyUtil.getAccessTokenPublicKey())
                .build();
    }

    // JWT REFRESH TOKEN =====================================

    @Bean("refreshJwkSource")
    JWKSource<SecurityContext> refreshJwkSource() {
        RSAKey rsaKey = new RSAKey.Builder(keyUtil.getRefreshTokenPublicKey())
                .privateKey(keyUtil.getRefreshTokenPrivateKey())
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector
                .select(jwkSet);
    }

    @Bean("refreshJwtEncoder")
    JwtEncoder refreshJwtEncoder(@Qualifier("refreshJwkSource") JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean("refreshJwtDecoder")
    JwtDecoder refreshJwtDecoder() throws JOSEException {
        return NimbusJwtDecoder
                .withPublicKey(keyUtil.getRefreshTokenPublicKey())
                .build();
    }

}