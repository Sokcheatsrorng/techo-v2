package co.istad.techco.techco.features.brand;

import co.istad.techco.techco.domain.Brand;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByName(String name);

    boolean existsByName(String name);

    Optional<Brand> findByUuid(String uuid);

    Page<Brand> findByNameContaining(String name, Pageable pageable);

}
