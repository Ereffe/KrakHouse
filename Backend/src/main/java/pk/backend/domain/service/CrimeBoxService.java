package pk.backend.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pk.backend.aplication.port.outbound.CrimeMapFactory;
import pk.backend.domain.model.CityMap.CityMap;

@RequiredArgsConstructor
@Service
public class CrimeBoxService implements MapService {

    private final CrimeMapFactory crimeMapFactory;

    public static final String TYPE = "CRIME";

    @Override
    public CityMap createMap(String mapType) {
        return crimeMapFactory.createMap();
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
