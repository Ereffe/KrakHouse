package pk.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.NoiseMapFactory;
import pk.backend.domain.model.box.NoiseBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.infrastructure.model.DiscreteData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class NoiseMapAdapter extends AbstractAdapter<Integer> implements NoiseMapFactory {

    @Override
    public List<DiscreteData<Integer>> fetchData() {
        //        TODO: 6 implement noise adapter
        List<DiscreteData<Integer>> mockData = new ArrayList<>();
        Random random = new Random(767676);
        for (int i = 0; i < 10; i++) {
            mockData.add(DiscreteData.<Integer>builder()
                    .latitude(50.118 + (50.124 - 50.118) * random.nextDouble())
                    .longitude(19.813 + (19.856 - 19.813) * random.nextDouble())
                    .value(30 + random.nextInt(61)) // 30-90 dB
                    .build());
        }
        return mockData;
    }

    @Override
    public BoxValue createBox(Integer value) {
        return new NoiseBox(value);
    }

    @Override
    public String assignDataProvider() {
        //        TODO: 6 implement noise adapter
        return "Mocked Noise Data Provider";
    }
}

