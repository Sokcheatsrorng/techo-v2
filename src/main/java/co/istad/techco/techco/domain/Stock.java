package co.istad.techco.techco.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private Integer quantityDelta;

    @Enumerated(EnumType.STRING)
    private Reason reason;

    private Boolean availability;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public enum Reason {
        PURCHASED,
        SOLD,
        RETURNED,
        ADJUSTED,
        DAMAGED
    }

}
