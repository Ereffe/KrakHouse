package pk.backend.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmlArchiveExtractorService {

    private final ImportStorage storage;

    public Path extractSingleGml(Path archivePath) {
        if (!isZip(archivePath)) {
            return archivePath;
        }

        Path extractionDir = storage.resolveExtractionDir(archivePath.getFileName().toString());
        List<Path> extractedGmlFiles = new ArrayList<>();

        try (InputStream inputStream = Files.newInputStream(archivePath);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                try {
                    if (!entry.isDirectory() && isGmlEntry(entry)) {
                        Path extractedFile = resolveSafeEntryPath(extractionDir, entry);
                        Files.createDirectories(extractedFile.getParent());

                        try (OutputStream outputStream = Files.newOutputStream(
                                extractedFile,
                                StandardOpenOption.CREATE,
                                StandardOpenOption.TRUNCATE_EXISTING,
                                StandardOpenOption.WRITE
                        )) {
                            zipInputStream.transferTo(outputStream);
                        }

                        extractedGmlFiles.add(extractedFile);
                        log.info("Extracted GML from archive: archive={}, entry={}, file={}",
                                archivePath, entry.getName(), extractedFile);
                    }
                } finally {
                    zipInputStream.closeEntry();
                }
            }

            if (extractedGmlFiles.isEmpty()) {
                throw new IllegalStateException("ZIP archive does not contain a .gml file: " + archivePath);
            }

            if (extractedGmlFiles.size() > 1) {
                throw new IllegalStateException("ZIP archive contains multiple .gml files: " + archivePath);
            }

            return extractedGmlFiles.getFirst();
        } catch (IOException e) {
            throw new UncheckedIOException("Could not extract GML archive: " + archivePath, e);
        }
    }

    private boolean isZip(Path path) {
        String fileName = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return fileName.endsWith(".zip");
    }

    private boolean isGmlEntry(ZipEntry entry) {
        return entry.getName().toLowerCase(Locale.ROOT).endsWith(".gml");
    }

    private Path resolveSafeEntryPath(Path extractionDir, ZipEntry entry) {
        Path resolved = extractionDir.resolve(entry.getName()).normalize();

        if (!resolved.startsWith(extractionDir.normalize())) {
            throw new IllegalStateException("ZIP entry escapes extraction directory: " + entry.getName());
        }

        return resolved;
    }
}
