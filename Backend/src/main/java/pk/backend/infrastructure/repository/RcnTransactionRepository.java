package pk.backend.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.backend.domain.model.rcn.RcnTransaction;

import java.util.Optional;

public interface RcnTransactionRepository extends JpaRepository<RcnTransaction, Long> {
    Optional<RcnTransaction> findByGmlId(String gmlId);
    Optional<RcnTransaction> findByTransactionCode(String transactionCode);
    boolean existsByGmlId(String gmlId);
}