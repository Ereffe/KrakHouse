package pk.backend.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.backend.domain.model.rcn.RcnUnresolvedReference;

import java.util.List;

public interface RcnUnresolvedReferenceRepository extends JpaRepository<RcnUnresolvedReference, Long> {

    void deleteBySourceTypeAndSourceGmlId(String sourceType, String sourceGmlId);

    List<RcnUnresolvedReference> findBySourceTypeAndSourceGmlId(String sourceType, String sourceGmlId);

    List<RcnUnresolvedReference> findByRelationName(String relationName);
}
