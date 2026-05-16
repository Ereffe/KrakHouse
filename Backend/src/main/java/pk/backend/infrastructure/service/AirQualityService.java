package pk.backend.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import pk.backend.infrastructure.adapter.AirQualityMapAdapter;
import pk.backend.infrastructure.dto.SensorDataResponseDto;
import pk.backend.infrastructure.dto.SensorResponseDto;
import pk.backend.infrastructure.dto.StationsRecordDto;
import pk.backend.infrastructure.dto.StationsResponseDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public SensorResponseDto getSensorsForStation(StationsRecordDto station) {
        var sensorsList = airQualityRestClient.get()
                .uri("/v1/rest/station/sensors/" + station.id())
                .retrieve()
                .body(SensorResponseDto.class);
        log.info("request: /v1/rest/station/sensors/" + station.id());
        return sensorsList;
    }


    public SensorDataResponseDto requestSensorData(long sensorId, int delayWeeks) {
        UriComponentsBuilder uriComponentBuilder = UriComponentsBuilder.newInstance();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String fromDate = LocalDateTime.now().minusWeeks(delayWeeks).minusDays(1).format(dateTimeFormatter);
        String toDate = LocalDateTime.now().minusWeeks(delayWeeks).format(dateTimeFormatter);

        String uri = uriComponentBuilder.path("/v1/rest/archivalData/getDataBySensor/" + sensorId)
                    .queryParam("size", 500)
                    .queryParam("dateFrom", fromDate)
                    .queryParam("dateTo", toDate)
                .build()
                .toString();


        var temp = airQualityRestClient.get()
                .uri(uri)
                .retrieve()
                .body(SensorDataResponseDto.class);

        log.info(temp.toString());

        return temp;
    }
}
