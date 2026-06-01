package pk.backend.domain.model.rcn;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(
        name = "rcn_unresolved_references",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_rcn_unresolved_reference",
                columnNames = {"source_type", "source_gml_id", "relation_name", "target_gml_id"}
        )
)
@NoArgsConstructor
public class RcnUnresolvedReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_type", nullable = false, length = 64)
    private String sourceType;

    @Column(name = "source_gml_id", nullable = false, length = 255)
    private String sourceGmlId;

    @Column(name = "relation_name", nullable = false, length = 128)
    private String relationName;

    @Column(name = "target_gml_id", nullable = false, length = 255)
    private String targetGmlId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public RcnUnresolvedReference(String sourceType, String sourceGmlId, String relationName, String targetGmlId) {
        this.sourceType = sourceType;
        this.sourceGmlId = sourceGmlId;
        this.relationName = relationName;
        this.targetGmlId = targetGmlId;
    }
}
