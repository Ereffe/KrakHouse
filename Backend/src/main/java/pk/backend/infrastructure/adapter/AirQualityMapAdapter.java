package pk.backend.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pk.backend.aplication.port.outbound.AirQualityMapFactory;
import pk.backend.domain.model.CityMap.CityMap;

@Component
@RequiredArgsConstructor
public class AirQualityMapAdapter implements AirQualityMapFactory {

    private final RestClient airQualityRestClient;

    @Override
    public CityMap createMap() {
//        TODO: 4 implement air quality adapter
//        TODO: 4 API required to show data source explicitly
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
