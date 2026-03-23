package pk.backend.aplication.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pk.backend.aplication.port.inbound.MapSource;
import pk.backend.aplication.port.outbound.ControllerPort;
import pk.backend.aplication.port.outbound.MapFilter;
import pk.backend.domain.CityMap.CityMap;
import pk.backend.domain.box.BoxValue;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MapService implements ControllerPort {

    private final MapSource mapSource;

    @Override
    public List<CityMap> getMaps(List<Class<? extends BoxValue>> mapTypes) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CityMap getFilteredMap(MapFilter mapFilter) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private CityMap getMap(Class<? extends BoxValue> mapType){
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
