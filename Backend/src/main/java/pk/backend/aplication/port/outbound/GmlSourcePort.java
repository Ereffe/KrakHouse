package pk.backend.aplication.port.outbound;

import java.nio.file.Path;

public interface GmlSourcePort {
    DownloadedGmlFile downloadLatest();

    record DownloadedGmlFile(
            Path path,
            String fileName,
            long sizeBytes,
            String sourceUrl
    ) {
    }
}