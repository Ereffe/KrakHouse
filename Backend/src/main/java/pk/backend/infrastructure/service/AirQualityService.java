package pk.backend.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import pk.backend.infrastructure.dto.SensorDataResponseDto;
import pk.backend.infrastructure.dto.SensorResponseDto;
import pk.backend.infrastructure.dto.StationsRecordDto;
import pk.backend.infrastructure.dto.StationsResponseDto;
import pk.backend.infrastructure.model.AirPollutionSensorsData;
import pk.backend.infrastructure.model.DiscreteData;
import pk.backend.infrastructure.utility.AirQualityMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AirQualityService {

    private final RestClient airQualityRestClient;

    public List<DiscreteData<Integer>> getAirQualityData() {
        List<AirPollutionSensorsData> airPollutionDataList = new ArrayList<>();
        List<StationsRecordDto> airQualityStations = getStations();

        for (var station : airQualityStations) {
            var temp = new AirPollutionSensorsData();
            temp.setId(station.id());
            temp.setLatitude(station.latitude());
            temp.setLongitude(station.longitude());

            airPollutionDataList.add(temp);
        }

        for (int i = 0; i < airQualityStations.size(); i++) {
            var station = airQualityStations.get(i);
            var sensorsList = getSensorsForStation(station);
            var stationData = airPollutionDataList.get(i);

            addSensorsForStation(stationData, sensorsList);
            log.info("Automatic station data: " + stationData.getSensors().entrySet());
        }

        return AirQualityMapper.mapToAQIList(airPollutionDataList);
    }


    private List<StationsRecordDto> getStations() {
        StationsResponseDto response = airQualityRestClient.get()
                .uri("/v1/rest/station/findAll?size=500")
                .retrieve()
                .body(StationsResponseDto.class);

        log.info("request: /v1/rest/station/findAll?size=500");
        log.info(response.stations().toString());

        return response.stations().stream()
                .filter(rec -> rec.cityName().equalsIgnoreCase("Krakow") || rec.cityName().contains("Krak"))
                .toList();
    }

    private SensorResponseDto getSensorsForStation(StationsRecordDto station) {
        var sensorsList = airQualityRestClient.get()
                .uri("/v1/rest/station/sensors/" + station.id())
                .retrieve()
                .body(SensorResponseDto.class);
        log.info("request: /v1/rest/station/sensors/" + station.id());
        return sensorsList;
    }


    private SensorDataResponseDto requestSensorData(long sensorId, int delayWeeks) {
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

    private void addSensorsForStation(AirPollutionSensorsData station, SensorResponseDto sensorList) {
        sensorList.sensors().forEach(sensorDto -> {
            log.info("try request sensor with id: " + sensorDto.sensorId());
            var sensorDataSequence = requestSensorData(sensorDto.sensorId(), 0);

            if (sensorDataSequence.readSequence().isEmpty()){
                log.info("try fallback request sensor with id: " + sensorDto.sensorId());
                sensorDataSequence = requestSensorData(sensorDto.sensorId(), 8);
            }

            if (sensorDataSequence.readSequence().isEmpty())
                return;

            station.addSensor(sensorDto.Indicator(), sensorDataSequence.readSequence());
        });
    }
}
