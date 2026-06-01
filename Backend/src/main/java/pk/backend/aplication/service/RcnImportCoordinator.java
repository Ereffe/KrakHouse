package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pk.backend.aplication.port.outbound.GmlSourcePort;
import pk.backend.infrastructure.config.ImportProperties;
import pk.backend.infrastructure.model.rcn.ImportFile;
import pk.backend.infrastructure.model.rcn.ImportJob;
import pk.backend.infrastructure.model.rcn.ImportJobStatus;
import pk.backend.infrastructure.repository.ImportJobRepository;
import pk.backend.infrastructure.service.GmlArchiveExtractorService;
import pk.backend.infrastructure.service.RcnJdbcBatchWriter;
import pk.backend.infrastructure.service.GmlStreamParser;
import pk.backend.infrastructure.service.parser.GmlParseResult;

import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class RcnImportCoordinator {

    private static final int MAX_ERROR_DETAILS_LENGTH = 4000;

    private final ImportProperties properties;
    private final ImportJobRepository importJobRepository;
    private final GmlSourcePort gmlSourcePort;
    private final GmlArchiveExtractorService archiveExtractorService;
    private final GmlStreamParser gmlStreamParser;
    private final RcnJdbcBatchWriter batchWriter;
    private final RelationLinkerService relationLinkerService;

    public RcnImportResult importLatest() {
        ImportJob job = importJobRepository.save(new ImportJob(properties.getSourceUrl()));
        log.info("Starting RCN import job: jobId={}, sourceUrl={}", job.getId(), properties.getSourceUrl());

        try {
            mark(job, ImportJobStatus.DOWNLOADING);
            GmlSourcePort.DownloadedGmlFile downloadedFile = downloadWithRetries();
            ImportFile importFile = new ImportFile(
                    downloadedFile.sourceUrl(),
                    downloadedFile.path().toString(),
                    downloadedFile.fileName()
            );
            importFile.markDownloaded(downloadedFile.sizeBytes(), null);
            job.addFile(importFile);
            mark(job, ImportJobStatus.DOWNLOADED);

            mark(job, ImportJobStatus.PARSING);
            Path gmlFile = archiveExtractorService.extractSingleGml(downloadedFile.path());
            batchWriter.prepareImport();
            GmlParseResult parseResult = gmlStreamParser.parse(gmlFile);

            mark(job, ImportJobStatus.LINKING);
            RelationLinkResult relationLinkResult = relationLinkerService.linkAll();

            mark(job, ImportJobStatus.DONE);
            log.info("Finished RCN import job: jobId={}", job.getId());

            return new RcnImportResult(
                    job.getId(),
                    downloadedFile.path(),
                    gmlFile,
                    parseResult.featureMemberCount(),
                    parseResult.handledObjectCount(),
                    parseResult.skippedObjectCount(),
                    relationLinkResult
            );
        } catch (RuntimeException e) {
            job.markFailed(truncateError(e));
            importJobRepository.save(job);
            log.error("RCN import job failed: jobId={}", job.getId(), e);
            throw e;
        }
    }

    private GmlSourcePort.DownloadedGmlFile downloadWithRetries() {
        int maxAttempts = Math.max(1, properties.getRetryCount() + 1);
        RuntimeException lastFailure = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return gmlSourcePort.downloadLatest();
            } catch (RuntimeException e) {
                lastFailure = e;
                log.warn("RCN GML download attempt failed: attempt={}, maxAttempts={}", attempt, maxAttempts, e);
            }
        }

        throw lastFailure;
    }

    private void mark(ImportJob job, ImportJobStatus status) {
        job.markStatus(status);
        importJobRepository.save(job);
    }

    private String truncateError(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            message = exception.getClass().getName();
        }

        if (message.length() <= MAX_ERROR_DETAILS_LENGTH) {
            return message;
        }

        return message.substring(0, MAX_ERROR_DETAILS_LENGTH);
    }
}
