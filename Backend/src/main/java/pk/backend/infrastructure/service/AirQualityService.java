package pk.backend.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pk.backend.infrastructure.dto.StationsRecordDto;
import pk.backend.infrastructure.dto.StationsResponseDto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AirQualityService {

    private final RestClient airQualityRestClient;

    public List<StationsRecordDto> getStations() {
        StationsResponseDto response = airQualityRestClient.get()
                .uri("/v1/rest/station/findAll?size=500")
                .retrieve()
                .body(StationsResponseDto.class);

        log.info("request: /v1/rest/station/findAll?size=500");
        log.info(response.stations().toString());

        return response.stations().stream()
                .filter(rec -> rec.cityName().equals("Kraków"))
                .toList();
    }
}
