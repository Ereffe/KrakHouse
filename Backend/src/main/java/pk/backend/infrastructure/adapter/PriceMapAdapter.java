package pk.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.PriceMapFactory;
import pk.backend.domain.model.box.PriceBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.infrastructure.model.DiscreteData;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class PriceMapAdapter extends AbstractAdapter<BigDecimal> implements PriceMapFactory {

    @Override
    public List<DiscreteData<BigDecimal>> fetchData() {
        //        TODO: 7 implement price adapter
        List<DiscreteData<BigDecimal>> mockData = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            mockData.add(DiscreteData.<BigDecimal>builder()
                    .latitude(50.118 + (50.124 - 50.118) * random.nextDouble())
                    .longitude(19.813 + (19.856 - 19.813) * random.nextDouble())
                    .value(BigDecimal.valueOf(1000 + random.nextInt(9000)))
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
        //        TODO: 7 implement price adapter
        return "Mocked Price Data Provider";
    }
}

