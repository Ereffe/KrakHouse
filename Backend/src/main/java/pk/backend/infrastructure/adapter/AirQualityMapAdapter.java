package pk.backend.infrastructure.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pk.backend.aplication.port.outbound.AirQualityMapFactory;
import pk.backend.domain.model.box.AirQualityBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.infrastructure.model.DiscreteData;
import pk.backend.infrastructure.service.AirQualityService;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class AirQualityMapAdapter extends AbstractAdapter<Integer> implements AirQualityMapFactory {

    private final AirQualityService airQualityService;

    @Override
    public List<DiscreteData<Integer>> fetchData() {
        return airQualityService.getAirQualityData();
    }

    @Override
    public BoxValue createBox(Integer value) {
        return new  AirQualityBox(value);
    }

    @Override
    public String assignDataProvider() {
        return airQualityService.getDataProvider();
    }

}
