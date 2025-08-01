package co.istad.techco.techco.domain;

import co.istad.techco.techco.domain.audit.Auditable;
import co.istad.techco.techco.features.mail.verificationtoken.VerificationToken;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private String firstName;

    private String lastName;

    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    private String phoneNumber;

    private String profile;

    /**
     * Indicates if the user's account is verified.
     */
    private Boolean isAccountVerified;

    /**
     * Indicates if the user's account is not expired.
     */
    private boolean isAccountNonExpired;

    /**
     * Indicates if the user's account is not locked.
     */
    private boolean isAccountNonLocked;

    /**
     * Indicates if the user's credentials are not expired.
     */
    private boolean isCredentialsNonExpired;

    private boolean isDeleted;

    private boolean isEmailVerified;

    @ManyToMany
    private List<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VerificationToken> verificationTokens;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Address address;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> carts;


}
