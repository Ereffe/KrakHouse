package pk.backend.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.AirQualityMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.infrastructure.dto.SensorResponseDto;
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
            var sensorsList = airQualityService.getSensorsForStation(station);

                addSensorsForStation(airQualityData.get(station.id()), sensorsList);
                log.info("Automatic station data" + airQualityData.get(station.id()).getSensors().entrySet());
        });
//        TODO: 4 add cache for optimization, because of API limits and response time
//        TODO: 4 implement air quality adapter
//        TODO: 4 API license requires to show data source explicitly
        throw new UnsupportedOperationException("implementation not finished yet");
    }

    private void addSensorsForStation(AirQualityData station, SensorResponseDto sensorList) {

        sensorList.sensors().forEach(sensorDto -> {
            log.info("try request sensor with id: " + sensorDto.sensorId());
            var sensorDataSequence = airQualityService.requestSensorData(sensorDto.sensorId(), 0);

            if (sensorDataSequence.readSequence().isEmpty()){
                log.info("try fallback request sensor with id: " + sensorDto.sensorId());
                sensorDataSequence = airQualityService.requestSensorData(sensorDto.sensorId(), 8);
            }

            if (sensorDataSequence.readSequence().isEmpty())
                return;

            station.addSensor(sensorDto.Indicator(), sensorDataSequence.readSequence());
        });
    }




}
