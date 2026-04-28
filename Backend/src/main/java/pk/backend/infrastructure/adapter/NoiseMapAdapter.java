package pk.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.NoiseMapFactory;
import pk.backend.domain.model.CityMap.CityMap;

@Component
public class NoiseMapAdapter implements NoiseMapFactory {

    @Override
    public CityMap createMap() {
        //        TODO: 6 implement crime adapter
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
