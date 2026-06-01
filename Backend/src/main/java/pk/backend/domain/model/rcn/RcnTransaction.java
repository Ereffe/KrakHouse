package pk.backend.domain.model.rcn;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "rcn_transactions")
@NoArgsConstructor
public class RcnTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String gmlId;

    @Column(length = 255)
    private String transactionCode;

    private LocalDate transactionDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    @Column(precision = 12, scale = 2)
    private BigDecimal area;

    @Column(precision = 15, scale = 2)
    private BigDecimal pricePerSquareMeter;

    @Column(length = 255)
    private String propertyRef;

    @ManyToOne(fetch = FetchType.LAZY)
    private RcnProperty property;

    public RcnTransaction(String gmlId) {
        this.gmlId = gmlId;
    }
}