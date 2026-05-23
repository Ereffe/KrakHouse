package pk.backend.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.AirQualityMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.CityMap.GridMap;
import pk.backend.domain.model.box.AirQualityBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
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

    private static final double LAT_STEP = 0.0005;
    private static final double LON_STEP = 0.001;

    @Override
    public CityMap createMap() {
        List<AirPollutionSensorsData> airPollutionDataList = new ArrayList<>();

        List<StationsRecordDto> airQualityStations = airQualityService.getStations();

        for (var station : airQualityStations) {
            var temp = new AirPollutionSensorsData();
            temp.setId(station.id());
            temp.setLatitude(station.latitude());
            temp.setLongitude(station.longitude());

            airPollutionDataList.add(temp);
        }

        for (int i = 0; i < airQualityStations.size(); i++) {
            var station = airQualityStations.get(i);
            var sensorsList = airQualityService.getSensorsForStation(station);
            var stationData = airPollutionDataList.get(i);

            addSensorsForStation(stationData, sensorsList);
            log.info("Automatic station data: " + stationData.getSensors().entrySet());
        }

        List<AirQualityData> aqiList = AirQualityMapper.mapToAQIList(airPollutionDataList);

        List<List<BoxValue>> boxMatrix = new ArrayList<>();

        for (double lat = GridMap.LATITUDE_LEFT_BORDER; lat + LAT_STEP <= GridMap.LATITUDE_RIGHT_BORDER; lat += LAT_STEP) {
            List<BoxValue> row = new ArrayList<>();
            for (double lon = GridMap.LONGITUDE_TOP_BORDER; lon + LON_STEP <= GridMap.LONGITUDE_BOTTOM_BORDER; lon += LON_STEP) {
                AirQualityData nearest = findNearestSensor(lat + LAT_STEP / 2, lon + LON_STEP / 2, aqiList);
                int aqiValue = (nearest != null) ? nearest.getAqi() : 0;
                row.add(new AirQualityBox(aqiValue));
            }
            boxMatrix.add(row);
        }

        return new GridMap(boxMatrix);
    }

    private AirQualityData findNearestSensor(double lat, double lon, List<AirQualityData> aqiList) {
        AirQualityData nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (AirQualityData data : aqiList) {
            double distance = Math.pow(lat - data.getLatitude(), 2) + Math.pow(lon - data.getLongitude(), 2);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = data;
            }
        }
        return nearest;
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
