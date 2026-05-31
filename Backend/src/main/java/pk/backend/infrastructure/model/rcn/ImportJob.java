package pk.backend.infrastructure.model.rcn;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "import_jobs")
@NoArgsConstructor
public class ImportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ImportJobStatus status = ImportJobStatus.CREATED;

    @Column(nullable = false, length = 2048)
    private String sourceUrl;

    @Column(nullable = false)
    private Instant startedAt = Instant.now();

    private Instant finishedAt;

    @Column(length = 4000)
    private String errorDetails;

    @OneToMany(mappedBy = "importJob", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImportFile> files = new ArrayList<>();

    public ImportJob(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public void addFile(ImportFile file) {
        files.add(file);
        file.setImportJob(this);
    }

    public void markStatus(ImportJobStatus status) {
        this.status = status;

        if (status == ImportJobStatus.DONE || status == ImportJobStatus.FAILED) {
            this.finishedAt = Instant.now();
        }
    }

    public void markFailed(String errorDetails) {
        this.errorDetails = errorDetails;
        markStatus(ImportJobStatus.FAILED);
    }
}
