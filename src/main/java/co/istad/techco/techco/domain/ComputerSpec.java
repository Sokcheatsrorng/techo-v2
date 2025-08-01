package co.istad.techco.techco.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "computer_specs")
public class ComputerSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String processor;

    private String ram;

    private String storage;

    private String gpu;

    private String os;

    private String screenSize;

    private String battery;

    @OneToOne
    @JsonBackReference
    private Product product;

}
