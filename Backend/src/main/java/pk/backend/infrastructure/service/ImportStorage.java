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
        log.info("Import storage ready: downloadDir={}, tempDir={}",
                properties.downloadDirPath(), properties.tempDirPath());
    }

    public Path resolveDownload(String fileName) {
        return properties.downloadDirPath().resolve(fileName);
    }

    public Path resolveTempPart(String fileName) {
        return properties.tempDirPath().resolve(fileName + ".part");
    }

    private void createDirectory(Path dir) {
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create import directory: " + dir, e);
        }
    }
}