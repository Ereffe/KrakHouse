package pk.backend.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.AirQualityMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.CityMap.GridMap;
import pk.backend.domain.model.box.AirQualityBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.infrastructure.model.AirQualityData;
import pk.backend.infrastructure.service.AirQualityService;

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
        List<AirQualityData> aqiList = airQualityService.getAirQualityData();

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
}
