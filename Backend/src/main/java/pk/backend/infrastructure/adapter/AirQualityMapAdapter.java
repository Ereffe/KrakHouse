package pk.backend.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.AirQualityMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.infrastructure.dto.SensorResponseDto;
import pk.backend.infrastructure.dto.StationsRecordDto;
import pk.backend.infrastructure.model.AirPollutionSensorsData;
import pk.backend.infrastructure.model.AirQualityData;
import pk.backend.infrastructure.service.AirQualityService;
import pk.backend.infrastructure.utility.AirQualityMapper;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class AirQualityMapAdapter implements AirQualityMapFactory {

    private final AirQualityService airQualityService;

    @Override
    public CityMap createMap() {
        List<AirPollutionSensorsData> airQualityData = new ArrayList<>();

        List<StationsRecordDto> airQualityStations = airQualityService.getStations();

        for (var station : airQualityStations) {
            var temp = new AirPollutionSensorsData();
            temp.setId(station.id());
            temp.setLatitude(station.latitude());
            temp.setLongitude(station.longitude());

            airQualityData.add(temp);
        }

        for (int i = 0; i < airQualityStations.size(); i++) {
            var station = airQualityStations.get(i);
            var sensorsList = airQualityService.getSensorsForStation(station);
            var stationData = airQualityData.get(i);

            addSensorsForStation(stationData, sensorsList);
            log.info("Automatic station data: " + stationData.getSensors().entrySet());
        }

        List<AirQualityData> aqiList = AirQualityMapper.mapToAQIList(airQualityData);



//        TODO: 4 add cache for optimization, because of API limits and response time
//        TODO: 4 implement air quality adapter
//        TODO: 4 API license requires to show data source explicitly
        throw new UnsupportedOperationException("implementation not finished yet");
    }

    private void addSensorsForStation(AirPollutionSensorsData station, SensorResponseDto sensorList) {

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
