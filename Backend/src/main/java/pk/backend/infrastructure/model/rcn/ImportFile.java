package pk.backend.infrastructure.model.rcn;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "import_files")
@NoArgsConstructor
public class ImportFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "import_job_id", nullable = false)
    private ImportJob importJob;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ImportFileStatus status = ImportFileStatus.DOWNLOADING;

    @Column(nullable = false, length = 2048)
    private String sourceUrl;

    @Column(nullable = false, length = 1024)
    private String filePath;

    @Column(length = 255)
    private String fileName;

    private Long sizeBytes;

    @Column(length = 64)
    private String checksumSha256;

    @Column(nullable = false)
    private Instant startedAt = Instant.now();

    private Instant finishedAt;

    @Column(length = 4000)
    private String errorDetails;

    public ImportFile(String sourceUrl, String filePath, String fileName) {
        this.sourceUrl = sourceUrl;
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public void markDownloaded(Long sizeBytes, String checksumSha256) {
        this.sizeBytes = sizeBytes;
        this.checksumSha256 = checksumSha256;
        this.status = ImportFileStatus.DOWNLOADED;
        this.finishedAt = Instant.now();
    }

    public void markFailed(String errorDetails) {
        this.errorDetails = errorDetails;
        this.status = ImportFileStatus.FAILED;
        this.finishedAt = Instant.now();
    }
}
