package pk.backend.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pk.backend.aplication.port.outbound.AirQualityMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.box.AirQualityBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;

@RequiredArgsConstructor
@Service
public class AirQualityBoxService implements MapService {

    private final AirQualityMapFactory airQualityMapFactory;

    public static final String TYPE = "AIR_QUALITY";
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 500;

    @Override
    public BoxValue createBoxValue(Number value) {
        return new AirQualityBox(value.intValue());
    }

    @Override
    public CityMap createMap() {
        return airQualityMapFactory.createMap();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public int getMinValue() {
        return MIN_VALUE;
    }

    @Override
    public int getMaxValue() {
        return MAX_VALUE;
    }
}
