package co.istad.techco.techco.features.supplier;

import co.istad.techco.techco.domain.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    Optional<Supplier> findByUuid(String uuid);

    boolean existsByName(String name);

    Page<Supplier> findByNameContainingIgnoreCase(String name, Pageable page);
}
