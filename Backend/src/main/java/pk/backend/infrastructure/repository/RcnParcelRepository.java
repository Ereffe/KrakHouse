package pk.backend.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.backend.domain.model.rcn.RcnParcel;

import java.util.Optional;

public interface RcnParcelRepository extends JpaRepository<RcnParcel, Long> {
    Optional<RcnParcel> findByGmlId(String gmlId);
    Optional<RcnParcel> findByParcelId(String parcelId);
    boolean existsByGmlId(String gmlId);
}