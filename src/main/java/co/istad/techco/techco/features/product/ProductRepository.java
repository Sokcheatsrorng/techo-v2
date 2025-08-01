package co.istad.techco.techco.features.product;

import co.istad.techco.techco.domain.Brand;
import co.istad.techco.techco.domain.Category;
import co.istad.techco.techco.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {


    Optional<Product> findByUuid(String productUuid);

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable page);


    Page<Product> findByCategory(Category category, Pageable page);

    Page<Product> findByBrand(Brand brand, Pageable page);

}
