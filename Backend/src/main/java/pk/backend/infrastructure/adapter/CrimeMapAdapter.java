package pk.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.CrimeMapFactory;
import pk.backend.domain.model.CityMap.CityMap;

@Component
public class CrimeMapAdapter implements CrimeMapFactory {

    @Override
    public CityMap createMap() {
        //        TODO: 4 implement crime adapter
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
