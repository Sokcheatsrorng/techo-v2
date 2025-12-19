package co.istad.techco.techco.domain;

import co.istad.techco.techco.converter.JsonConverter;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private String name;

    private String description;

//    @Column(columnDefinition = "TEXT")
//    @Convert(converter = ComputerSpecConverter.class)
//    private ComputerSpec computerSpec;

    private Integer stockQuantity;

    private BigDecimal priceIn;

    private BigDecimal priceOut;

    private Double discount;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private List<ColorOption> color;

    private String thumbnail;

    private String warranty;

    private Boolean availability;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private List<String> images;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Stock> stocks;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private ComputerSpec computerSpec;

    public List<ColorOption> getColorOptions() {
        return color;
    }

  
}
