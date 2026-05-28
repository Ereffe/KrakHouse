package pk.backend.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pk.backend.aplication.port.outbound.CrimeMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.box.CrimeBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;

@RequiredArgsConstructor
@Service
public class CrimeBoxService implements MapService {

    private final CrimeMapFactory crimeMapFactory;

    public static final String TYPE = "CRIME";
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;

    @Override
    public BoxValue createBoxValue(Number value) {
        if (value == null) return new CrimeBox(0f);
        return new CrimeBox(value.floatValue() / 100f);
    }

    @Override
    public CityMap createMap() {
        return crimeMapFactory.createMap();
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

