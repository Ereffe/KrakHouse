package pk.backend.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.backend.domain.model.rcn.RcnProperty;

import java.util.Optional;

public interface RcnPropertyRepository extends JpaRepository<RcnProperty, Long> {
    Optional<RcnProperty> findByGmlId(String gmlId);
    boolean existsByGmlId(String gmlId);
}