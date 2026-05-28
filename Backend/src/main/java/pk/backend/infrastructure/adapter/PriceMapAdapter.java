package pk.backend.infrastructure.adapter;

import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.PriceMapFactory;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.domain.model.box.PriceBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.infrastructure.model.DiscreteData;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PriceMapAdapter extends AbstractAdapter<BigDecimal> implements PriceMapFactory {

    @Override
    public List<DiscreteData<BigDecimal>> fetchData() {
        //        TODO: 7 implement price adapter
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public BoxValue createBox(BigDecimal value) {
        return new PriceBox(value);
    }

    @Override
    public String assignDataProvider() {
        //        TODO: 7 implement price adapter
        return "";
    }
}
