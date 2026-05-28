package pk.backend.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pk.backend.aplication.port.outbound.NoiseMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.box.NoiseBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;

@RequiredArgsConstructor
@Service
public class NoiseBoxService implements MapService {

    private final NoiseMapFactory noiseMapFactory;

    public static final String TYPE = "NOISE";
    private static final int MIN_VALUE = 30;
    private static final int MAX_VALUE = 90;

    @Override
    public BoxValue createBoxValue(Number value) {
        return new NoiseBox(value.intValue());
    }

    @Override
    public CityMap createMap() {
        return noiseMapFactory.createMap();
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
