package pk.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.CrimeMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.box.CrimeBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.infrastructure.model.DiscreteData;

import java.util.List;

@Component
public class CrimeMapAdapter extends AbstractAdapter<Integer> implements CrimeMapFactory {

    @Override
    public List<DiscreteData<Integer>> fetchData() {
        //        TODO: 5 implement crime adapter
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public BoxValue createBox(Integer value) {
        return new CrimeBox(value);
    }

    @Override
    public String assignDataProvider() {
        //        TODO: 5 implement crime adapter
        return "";
    }
}
