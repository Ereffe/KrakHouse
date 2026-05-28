package pk.backend.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pk.backend.aplication.port.outbound.PriceMapFactory;
import pk.backend.domain.model.CityMap.CityMap;

@RequiredArgsConstructor
@Service
public class PriceBoxService implements MapService {

    private final PriceMapFactory priceMapFactory;

    public static final String TYPE = "PRICE";
    private static final int MIN_VALUE = 1_000;
    private static final int MAX_VALUE = 100_000;

    @Override
    public CityMap createMap(String mapType) {
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
