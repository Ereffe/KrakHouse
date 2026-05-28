package pk.backend.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pk.backend.aplication.port.outbound.PriceMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.box.PriceBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class PriceBoxService implements MapService {

    private final PriceMapFactory priceMapFactory;

    public static final String TYPE = "PRICE";
    private static final int MIN_VALUE = 1_000;
    private static final int MAX_VALUE = 100_000;

    @Override
    public BoxValue createBoxValue(Number value) {
        if (value == null) return new PriceBox(BigDecimal.ZERO);
        return new PriceBox(BigDecimal.valueOf(value.doubleValue()));
    }

    @Override
    public CityMap createMap() {
        return priceMapFactory.createMap();
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

