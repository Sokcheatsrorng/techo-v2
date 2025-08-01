package co.istad.techco.techco.init;

import co.istad.techco.techco.domain.Authority;
import co.istad.techco.techco.domain.Role;
import co.istad.techco.techco.features.roleauthority.AuthorityRepository;
import co.istad.techco.techco.features.roleauthority.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleAuthorityInit {

    private final RoleRepository roleRepository;
    private final AuthorityRepository authorityRepository;

    @PostConstruct
    void initRoleAuthority() {

        if (roleRepository.count() < 1) {

            Authority userRead = new Authority();
            userRead.setName("user:read");

            Authority userWrite = new Authority();
            userWrite.setName("user:write");

            Authority adminRead = new Authority();
            adminRead.setName("admin:read");

            Authority adminWrite = new Authority();
            adminWrite.setName("admin:write");

            authorityRepository.saveAll(
                    List.of(userRead, userWrite, adminRead, adminWrite)
            );

            // Roles
            Role admin = new Role();
            admin.setName("ADMIN");
            admin.setAuthorities(List.of(
                    adminRead, adminWrite, userRead, userWrite
            ));

            Role user = new Role();
            user.setName("USER");
            user.setAuthorities(List.of(
                    userRead, userWrite
            ));

            // Save roles to repository
            roleRepository.saveAll(List.of(
                    admin, user
            ));

        }

    }

}
