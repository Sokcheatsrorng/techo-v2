package co.istad.techco.techco.features.roleauthority;

import co.istad.techco.techco.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
}
