package pk.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.NoiseMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.box.NoiseBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.infrastructure.model.DiscreteData;

import java.util.List;

@Component
public class NoiseMapAdapter extends AbstractAdapter<Integer> implements NoiseMapFactory {

    @Override
    public List<DiscreteData<Integer>> fetchData() {
        //        TODO: 6 implement noise adapter
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BoxValue createBox(Integer value) {
        return new NoiseBox(value);
    }

    @Override
    public String assignDataProvider() {
        //        TODO: 6 implement noise adapter
        return "";
    }
}
