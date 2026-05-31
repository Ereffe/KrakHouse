package pk.backend.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.backend.domain.model.rcn.RcnBuilding;

import java.util.Optional;

public interface RcnBuildingRepository extends JpaRepository<RcnBuilding, Long> {
    Optional<RcnBuilding> findByGmlId(String gmlId);
    Optional<RcnBuilding> findByBuildingId(String buildingId);
    boolean existsByGmlId(String gmlId);
}