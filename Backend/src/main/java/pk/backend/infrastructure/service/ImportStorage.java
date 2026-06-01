package pk.backend.infrastructure.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pk.backend.infrastructure.config.ImportProperties;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImportStorage {

    private final ImportProperties properties;

    @PostConstruct
    void init() {
        createDirectory(properties.downloadDirPath());
        createDirectory(properties.tempDirPath());
        createDirectory(extractDirPath());
        log.info("Import storage ready: downloadDir={}, tempDir={}",
                properties.downloadDirPath(), properties.tempDirPath());
    }

    public Path resolveDownload(String fileName) {
        return properties.downloadDirPath().resolve(fileName);
    }

    public Path resolveTempPart(String fileName) {
        return properties.tempDirPath().resolve(fileName + ".part");
    }

    public Path resolveExtractionDir(String archiveFileName) {
        String safeName = sanitizeFileName(archiveFileName);
        int extensionIndex = safeName.lastIndexOf('.');
        String baseName = extensionIndex > 0 ? safeName.substring(0, extensionIndex) : safeName;
        Path extractionDir = extractDirPath().resolve(baseName);
        createDirectory(extractionDir);
        return extractionDir;
    }

    private Path extractDirPath() {
        return properties.tempDirPath().resolve("extracted");
    }

    private String sanitizeFileName(String fileName) {
        return Path.of(fileName).getFileName().toString()
                .replace("\\", "_")
                .replace("/", "_")
                .replace("..", "_");
    }

    private void createDirectory(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create import directory: " + dir, e);
        }
    }
}
