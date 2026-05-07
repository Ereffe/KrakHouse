package pk.backend.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import pk.backend.aplication.port.outbound.AirQualityMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.infrastructure.dto.Pm10ResponseDto;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class AirQualityMapAdapter implements AirQualityMapFactory {

    private final RestClient airQualityRestClient;
    private final UriBuilder uriComponentBuilder;

    private final static String GET_PM10_DATA = "/v1/rest/aggregate/getAggregatePm10Data";

    @Override
    public CityMap createMap() {
        URI pm10Uri = uriComponentBuilder.path(GET_PM10_DATA)
                .queryParam("size", 500)
                .queryParam("filter[powiat]", "Kraków")
                .queryParam("filter[wojewodztwo]", "MAŁOPOLSKIE")
                .build();

        Pm10ResponseDto response = airQualityRestClient.get()
                .uri(pm10Uri)
                .retrieve()
                .body(Pm10ResponseDto.class);

//        TODO: 4 implement air quality adapter
//        TODO: 4 API required to show data source explicitly
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
