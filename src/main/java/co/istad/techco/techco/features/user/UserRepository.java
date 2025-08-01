package co.istad.techco.techco.features.user;

import co.istad.techco.techco.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsernameOrEmail(@NotBlank @Email String username, @NotBlank @Email String email);

    Optional<User> findByEmail(String email);

    Page<User> findUsersByUsernameContaining( String username, Pageable pageable);

    Optional<User> findByUuid(String userUuid);

    boolean existsByUuid(String uuid);

    @Modifying
    @Query("UPDATE User u SET u.isDeleted = TRUE WHERE u.uuid = ?1")
    void deleteByUuid(String uuid);

}
