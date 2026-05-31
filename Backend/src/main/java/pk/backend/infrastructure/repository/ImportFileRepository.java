package pk.backend.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pk.backend.infrastructure.model.rcn.ImportFile;
import pk.backend.infrastructure.model.rcn.ImportFileStatus;

import java.util.List;
import java.util.Optional;

public interface ImportFileRepository extends JpaRepository<ImportFile, Long> {
    Optional<ImportFile> findByFilePath(String filePath);
    List<ImportFile> findByStatus(ImportFileStatus status);
    List<ImportFile> findByImportJobId(Long importJobId);
}