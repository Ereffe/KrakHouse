package pk.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.AirQualityMapFactory;
import pk.backend.domain.model.CityMap.CityMap;

@Component
public class AirQualityMapAdapter implements AirQualityMapFactory {

    @Override
    public CityMap createMap() {
//        TODO: 4 implement air quality adapter
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
