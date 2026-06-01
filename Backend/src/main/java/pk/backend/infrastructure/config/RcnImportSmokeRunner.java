package pk.backend.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pk.backend.aplication.service.RcnImportCoordinator;

@Component
@Profile("import-smoke")
@RequiredArgsConstructor
public class RcnImportSmokeRunner implements CommandLineRunner {

    private final RcnImportCoordinator coordinator;

    @Override
    public void run(String... args) {
        coordinator.importLatest();
    }
}