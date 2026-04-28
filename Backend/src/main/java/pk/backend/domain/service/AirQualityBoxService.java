package pk.backend.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pk.backend.aplication.port.outbound.AirQualityMapFactory;
import pk.backend.domain.model.CityMap.CityMap;

@RequiredArgsConstructor
@Service
public class AirQualityBoxService implements MapService {

    private final AirQualityMapFactory airQualityMapFactory;

    public static final String TYPE = "AIR_QUALITY";

    @Override
    public CityMap createMap(String mapType) {
        return airQualityMapFactory.createMap();
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
