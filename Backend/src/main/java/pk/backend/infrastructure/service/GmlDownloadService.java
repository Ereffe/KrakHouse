package pk.backend.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pk.backend.aplication.port.outbound.GmlSourcePort;
import pk.backend.infrastructure.config.ImportProperties;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmlDownloadService implements GmlSourcePort {

    private final WebClient.Builder webClientBuilder;
    private final ImportProperties properties;
    private final ImportStorage storage;

    @Override
    public DownloadedGmlFile downloadLatest() {
        String sourceUrl = properties.getSourceUrl();
        String fileName = buildFileName(sourceUrl);

        Path partPath = storage.resolveTempPart(fileName);
        Path finalPath = storage.resolveDownload(fileName);

        try {
            Files.deleteIfExists(partPath);

            log.info("Downloading GML from {} to {}", sourceUrl, partPath);

            Flux<DataBuffer> body = webClientBuilder.build()
                    .get()
                    .uri(sourceUrl)
                    .retrieve()
                    .bodyToFlux(DataBuffer.class);

            DataBufferUtils.write(
                    body,
                    partPath,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE
            ).block();

            moveCompletedFile(partPath, finalPath);

            long sizeBytes = Files.size(finalPath);

            log.info("Downloaded GML file: path={}, sizeBytes={}", finalPath, sizeBytes);

            return new DownloadedGmlFile(finalPath, fileName, sizeBytes, sourceUrl);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not download GML file from " + sourceUrl, e);
        } catch (RuntimeException e) {
            tryDelete(partPath);
            throw e;
        }
    }

    private String buildFileName(String sourceUrl) {
        String rawName = Path.of(URI.create(sourceUrl).getPath()).getFileName().toString();

        String safeName = rawName
                .replace("\\", "_")
                .replace("/", "_")
                .replace("..", "_");

        if (safeName.isBlank()) {
            safeName = "rcn.gml";
        }

        return Instant.now().toEpochMilli() + "-" + safeName;
    }

    private void moveCompletedFile(Path partPath, Path finalPath) throws IOException {
        try {
            Files.move(partPath, finalPath, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException atomicMoveFailure) {
            Files.move(partPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void tryDelete(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.warn("Could not delete incomplete GML download: {}", path, e);
        }
    }
}