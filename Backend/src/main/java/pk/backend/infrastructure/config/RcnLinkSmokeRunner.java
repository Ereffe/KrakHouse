package pk.backend.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pk.backend.aplication.service.PricePointBuilderService;

@Component
@Profile("link-smoke")
@RequiredArgsConstructor
public class RcnLinkSmokeRunner implements CommandLineRunner {

    private final PricePointBuilderService pricePointBuilderService;

    @Override
    public void run(String... args) {
        pricePointBuilderService.rebuildPricePoints();
    }
}
