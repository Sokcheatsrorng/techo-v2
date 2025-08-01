package co.istad.techco.techco.features.auth;

import co.istad.techco.techco.features.auth.dto.AuthResponse;
import co.istad.techco.techco.features.auth.dto.LoginRequest;
import co.istad.techco.techco.features.auth.dto.RefreshTokenRequest;
import jakarta.validation.Valid;

public interface AuthService {
    AuthResponse login(@Valid LoginRequest loginRequest);

    AuthResponse refresh(@Valid RefreshTokenRequest refreshTokenRequest);
}
