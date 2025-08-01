package co.istad.techco.techco.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uuid;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private CurrencyEnum currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus transactionStatus;

    private LocalDateTime paymentDate;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public enum CurrencyEnum {
        USD,
        KHR
    }

    public enum PaymentStatus {
        PENDING,  // Initial state before confirmation
        SUCCESS,
        FAILED
    }

    public enum PaymentMethod {
        BANK_TRANSFER,
        QR_CODE,
        CASH
    }

}

