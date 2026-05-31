package pk.backend.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.backend.domain.model.rcn.RcnLocal;

import java.util.Optional;

public interface RcnLocalRepository extends JpaRepository<RcnLocal, Long> {
    Optional<RcnLocal> findByGmlId(String gmlId);
    Optional<RcnLocal> findByLocalNumber(String localNumber);
    boolean existsByGmlId(String gmlId);
}