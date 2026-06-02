package pk.backend.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.PriceMapFactory;
import pk.backend.domain.model.box.PriceBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.infrastructure.model.DiscreteData;
import pk.backend.infrastructure.service.RcnFinalPriceService;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceMapAdapter extends AbstractAdapter<BigDecimal> implements PriceMapFactory {

    private final RcnFinalPriceService rcnFinalPriceService;

    @Override
    public List<DiscreteData<BigDecimal>> fetchData() {
        return rcnFinalPriceService.getPriceData();
    }

    @Override
    public BoxValue createBox(BigDecimal value) {
        return new PriceBox(value);
    }

    @Override
    public String assignDataProvider() {
        return rcnFinalPriceService.getDataProvider();
    }
}

