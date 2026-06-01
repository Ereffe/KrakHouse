package pk.backend.domain.model.rcn;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rcn_addresses")
@NoArgsConstructor
public class RcnAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String gmlId;

    @Column(length = 255)
    private String city;

    @Column(length = 255)
    private String street;

    @Column(length = 100)
    private String buildingNumber;

    @Column(length = 100)
    private String apartmentNumber;

    @Column(length = 20)
    private String postalCode;

    public RcnAddress(String gmlId) {
        this.gmlId = gmlId;
    }
}