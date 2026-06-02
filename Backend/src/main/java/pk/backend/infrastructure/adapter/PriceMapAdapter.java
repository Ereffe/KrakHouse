package pk.backend.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.PriceMapFactory;
import pk.backend.domain.model.box.PriceBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.infrastructure.model.DiscreteData;
import pk.backend.infrastructure.service.RcnFinalPriceService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceMapAdapter extends AbstractAdapter<BigDecimal> implements PriceMapFactory {

    private final RcnFinalPriceService rcnFinalPriceService;

    @Override
    public List<DiscreteData<BigDecimal>> fetchData() {
        List<DiscreteData<BigDecimal>> mockData = new ArrayList<>();

        Random random = new Random(12345L);

        for (int i = 0; i < 100; i++) {
            double lat;
            double lon;
            double priceValue;

            if (random.nextDouble() < 0.20) {
                lat = 50.121 + (50.123 - 50.121) * random.nextDouble();
                lon = 19.830 + (19.840 - 19.830) * random.nextDouble();
                priceValue = 40000 + (60000 - 40000) * random.nextDouble();
            }

            else {
                lat = 50.118 + (50.124 - 50.118) * random.nextDouble();
                lon = 19.813 + (19.856 - 19.813) * random.nextDouble();

                priceValue = 15000 + (random.nextGaussian() * 4000);

                if (priceValue < 5000) {
                    priceValue = 5000 + random.nextDouble() * 2000;
                } else if (priceValue > 40000) {
                    priceValue = 40000 - random.nextDouble() * 2000;
                }
            }

            BigDecimal price = BigDecimal.valueOf(priceValue).setScale(2, RoundingMode.HALF_UP);

            mockData.add(DiscreteData.<BigDecimal>builder()
                    .latitude(lat)
                    .longitude(lon)
                    .value(price)
                    .build());
        }
        return mockData;
    }

    @Override
    public BoxValue createBox(BigDecimal value) {
        return new PriceBox(value);
    }

    @Override
    public String assignDataProvider() {
        return rcnFinalPriceService.getDataProvider();
    }
}

