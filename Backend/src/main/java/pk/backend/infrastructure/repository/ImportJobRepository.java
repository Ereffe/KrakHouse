package pk.backend.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.backend.infrastructure.model.rcn.ImportJob;
import pk.backend.infrastructure.model.rcn.ImportJobStatus;

import java.util.List;

public interface ImportJobRepository extends JpaRepository<ImportJob, Long> {
    List<ImportJob> findByStatus(ImportJobStatus status);
    List<ImportJob> findByStatusIn(List<ImportJobStatus> statuses);
}