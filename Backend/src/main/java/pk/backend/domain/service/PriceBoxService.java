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

    @Override
    public CityMap createMap(String mapType) {
        return priceMapFactory.createMap();
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
