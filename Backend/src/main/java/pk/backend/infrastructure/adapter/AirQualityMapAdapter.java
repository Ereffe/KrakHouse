package pk.backend.infrastructure.adapter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import pk.backend.aplication.port.outbound.AirQualityMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.infrastructure.dto.SingleSensorReadDto;
import pk.backend.infrastructure.dto.StationsRecordDto;
import pk.backend.infrastructure.model.AirQualityData;
import pk.backend.infrastructure.service.AirQualityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class AirQualityMapAdapter implements AirQualityMapFactory {

    private final AirQualityService airQualityService;

    private final RestClient airQualityRestClient;
//    private final UriBuilder uriComponentBuilder;

    @Override
    public CityMap createMap() {
        Map<Long, AirQualityData> airQualityData = new HashMap<>();

        var airQualityStations = airQualityService.getStations();

        for (StationsRecordDto station : airQualityStations) {
            var temp = new AirQualityData();
            temp.setId(station.id());
            temp.setLatitude(station.latitude());
            temp.setLongitude(station.longitude());

            airQualityData.put(station.id(), temp);
        }

        airQualityStations.forEach(rec -> {
            var temp = airQualityRestClient.get()
                    .uri("/v1/rest/station/sensors/" + rec.id())
                    .retrieve()
                    .body(SensorResponseDto.class);
            log.info("request: /v1/rest/station/sensors/"  + rec.id());

            temp.sensors.forEach(sensorDto -> {
                SensorDataResponseDto sensorValue = airQualityRestClient.get()
                        .uri("/v1/rest/data/getData/" + sensorDto.sensorId + "?size=500")
                        .retrieve()
                        .body(SensorDataResponseDto.class);
                log.info("request: /v1/rest/data/getData/" + sensorDto.sensorId + "?size=500");
                log.info(sensorValue.sensors().toString());

//                log.info("End of sensors list for " +  sensorDto.sensorId);
                var stationsMap = airQualityData.get(rec.id());
                stationsMap.addSensor(sensorDto.Indicator(), sensorValue.sensors);
            });
            log.info("CurrentSensors count: " + airQualityData.size());
        });
//        TODO: implement flow for retrieving data that is input manually after 4-8 weeks


//        URI pm10Uri = uriComponentBuilder.path(GET_PM10_DATA)
//                .queryParam("size", 500)
//                .queryParam("filter[powiat]", "Kraków")
//                .queryParam("filter[wojewodztwo]", "MAŁOPOLSKIE")
//                .build();


//        TODO: 4 implement air quality adapter
//        TODO: 4 API required to show data source explicitly
        throw new UnsupportedOperationException("implementation not finished yet");
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
    record SensorDataResponseDto(
            @JsonProperty("Lista danych pomiarowych")
            List<SingleSensorReadDto> sensors
    ) {
    }


}
