package pk.backend.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pk.backend.aplication.port.outbound.NoiseMapFactory;
import pk.backend.domain.model.CityMap.CityMap;

@RequiredArgsConstructor
@Service
public class NoiseBoxService implements MapService {

    private final NoiseMapFactory noiseMapFactory;

    public static final String TYPE = "NOISE";

    @Override
    public CityMap createMap(String mapType) {
        return noiseMapFactory.createMap();
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
