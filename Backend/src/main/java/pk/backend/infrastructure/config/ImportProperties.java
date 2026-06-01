package pk.backend.infrastructure.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "rcn.import")
public class ImportProperties {

    @NotBlank
    private String sourceUrl;

    @NotBlank
    private String downloadDir;

    @NotBlank
    private String tempDir;

    @Min(0)
    private int retryCount = 3;

    @Min(1)
    private int batchSize = 2000;

    private String cron = "";

    public Path downloadDirPath() {
        return Path.of(downloadDir);
    }

    public Path tempDirPath() {
        return Path.of(tempDir);
    }

    public boolean schedulingEnabled() {
        return cron != null && !cron.isBlank();
    }
}
