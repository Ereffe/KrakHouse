package pk.backend.infrastructure.adapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import pk.backend.aplication.port.outbound.AirQualityMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.infrastructure.dto.SingleSensorReadDto;
import pk.backend.infrastructure.dto.StationsRecordDto;
import pk.backend.infrastructure.model.AirQualityData;
import pk.backend.infrastructure.service.AirQualityService;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class AirQualityMapAdapter implements AirQualityMapFactory {

    private final AirQualityService airQualityService;

    private final RestClient airQualityRestClient;

    @Override
    public CityMap createMap() {
        Map<Long, AirQualityData> airQualityData = new HashMap<>();

        List<StationsRecordDto> airQualityStations = airQualityService.getStations();

        for (var station : airQualityStations) {
            var temp = new AirQualityData();
            temp.setId(station.id());
            temp.setLatitude(station.latitude());
            temp.setLongitude(station.longitude());

            airQualityData.put(station.id(), temp);
        }

        airQualityStations.forEach(station -> {
            var sensorsList = airQualityRestClient.get()
                    .uri("/v1/rest/station/sensors/" + station.id())
                    .retrieve()
                    .body(SensorResponseDto.class);
            log.info("request: /v1/rest/station/sensors/" + station.id());

            try {
                addSensorsForStation(airQualityData.get(station.id()), sensorsList, 0);
                log.info("Automatic station data" + airQualityData.get(station.id()).getSensors().entrySet());
            } catch (HttpClientErrorException e) { //TODO: catch more specific exception
                try {
                    log.info(e.getMessage());
                    addSensorsForStation(airQualityData.get(station.id()), sensorsList, 8);
                    log.info("Manual station data" + airQualityData.get(station.id()).getSensors().entrySet());
                } catch (Exception ex) {
                    log.info("Unable to fetch station data" + ex.getMessage());
                }
            }
        });
//        TODO: implement flow for retrieving data that is input manually after 4-8 weeks

//        TODO: 4 implement air quality adapter
//        TODO: 4 API license requires to show data source explicitly
        throw new UnsupportedOperationException("implementation not finished yet");
    }

    private void addSensorsForStation(AirQualityData station, SensorResponseDto sensorList, int delayWeeks) {

        sensorList.sensors.forEach(sensorDto -> {

            var sensorDataSequence = airQualityService.requestSensorData(sensorDto.sensorId(), delayWeeks);

            station.addSensor(sensorDto.Indicator(), sensorDataSequence.readSequence);
        });
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SensorResponseDto(
            @JsonProperty("Lista stanowisk pomiarowych dla podanej stacji")
            List<SensorDto> sensors
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record SensorDto(
            @JsonProperty("Identyfikator stanowiska")
            Long sensorId,

            @JsonProperty("Wskaźnik - wzór")
            String Indicator
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SensorDataResponseDto(
            @JsonProperty("Lista archiwalnych wyników pomiarów")
            List<SingleSensorReadDto> readSequence
    ) {
    }


}
