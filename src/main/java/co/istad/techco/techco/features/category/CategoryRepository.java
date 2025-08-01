package co.istad.techco.techco.features.category;

import co.istad.techco.techco.domain.Category;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {


    Optional<Category> findByName(String name);

    Optional<Category> findByUuid(String uuid);

    boolean existsByName(String name);

    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageRequest);
}
