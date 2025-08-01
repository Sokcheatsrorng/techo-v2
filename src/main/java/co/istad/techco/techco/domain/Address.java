package co.istad.techco.techco.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private String addressLine1;

    private String addressLine2;

    private String road;

    private String linkAddress;

    @OneToOne
    private Supplier supplier;

    @OneToOne
    private User user;

}
