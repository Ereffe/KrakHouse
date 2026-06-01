package pk.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.CrimeMapFactory;
import pk.backend.domain.model.box.CrimeBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.infrastructure.model.DiscreteData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class CrimeMapAdapter extends AbstractAdapter<Float> implements CrimeMapFactory {

    @Override
    public List<DiscreteData<Float>> fetchData() {
        //        TODO: 5 implement crime adapter
        List<DiscreteData<Float>> mockData = new ArrayList<>();
        Random random = new Random(676767);
        
        // Mocking some points around Krakow
        for (int i = 0; i < 10; i++) {
            mockData.add(DiscreteData.<Float>builder()
                    .latitude(50.118 + (50.124 - 50.118) * random.nextDouble())
                    .longitude(19.813 + (19.856 - 19.813) * random.nextDouble())
                    .value(random.nextFloat())
                    .build());
        }
        
        return mockData;
    }

    @Override
    public BoxValue createBox(Float value) {
        return new CrimeBox(value);
    }

    @Override
    public String assignDataProvider() {
        //        TODO: 5 implement crime adapter
        return "Mocked Crime Data Provider";
    }
}

