package pk.backend.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.CityMap.GridMap;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.infrastructure.model.DiscreteData;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractAdapter<T extends Number & Comparable<T>>{

    private static final double LAT_STEP = 0.0005;
    private static final double LON_STEP = 0.001;

    public abstract List<DiscreteData<T>> fetchData();
    public abstract BoxValue createBox(T value);
    public abstract String assignDataProvider();

    public CityMap createMap() {
        List<DiscreteData<T>> aqiList = fetchData();

        List<List<BoxValue>> boxMatrix = new ArrayList<>();

        for (double lat = GridMap.LATITUDE_LEFT_BORDER; lat + LAT_STEP <= GridMap.LATITUDE_RIGHT_BORDER; lat += LAT_STEP) {
            List<BoxValue> row = new ArrayList<>();
            for (double lon = GridMap.LONGITUDE_TOP_BORDER; lon + LON_STEP <= GridMap.LONGITUDE_BOTTOM_BORDER; lon += LON_STEP) {
                DiscreteData<T> nearest = findNearestSensor(lat + LAT_STEP / 2, lon + LON_STEP / 2, aqiList);
                row.add(createBox(nearest.getValue()));
            }
            boxMatrix.add(row);
        }

        return new GridMap(boxMatrix, assignDataProvider());
    }

    private DiscreteData<T> findNearestSensor(double lat, double lon, List<DiscreteData<T>> aqiList) {
        DiscreteData<T> nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (DiscreteData<T> data : aqiList) {
            double distance = Math.pow(lat - data.getLatitude(), 2) + Math.pow(lon - data.getLongitude(), 2);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = data;
            }
        }
        return nearest;
    }
}
