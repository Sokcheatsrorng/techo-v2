package co.istad.techco.techco.domain.audit;


import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EntityAuditorAware implements AuditorAware<String> {

    @NonNull
    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            authentication.isAuthenticated();
        }

        assert authentication != null;
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return Optional.of(jwt.getId());
        }

        if (!authentication.isAuthenticated()) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return Optional.of(jwt.getId());
        }

        return Optional.empty();
    }

}
