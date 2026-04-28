package pk.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.PriceMapFactory;
import pk.backend.domain.model.CityMap.CityMap;

@Component
public class PriceMapAdapter implements PriceMapFactory {

    @Override
    public CityMap createMap() {
        //        TODO: 7 implement price adapter
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
