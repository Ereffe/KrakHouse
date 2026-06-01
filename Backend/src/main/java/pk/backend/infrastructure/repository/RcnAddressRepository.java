package pk.backend.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.backend.domain.model.rcn.RcnAddress;

import java.util.Optional;

public interface RcnAddressRepository extends JpaRepository<RcnAddress, Long> {
    Optional<RcnAddress> findByGmlId(String gmlId);
    boolean existsByGmlId(String gmlId);
}