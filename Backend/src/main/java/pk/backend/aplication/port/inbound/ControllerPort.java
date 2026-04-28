package pk.backend.aplication.port.inbound;

import pk.backend.aplication.port.outbound.MapFilter;
import pk.backend.domain.CityMap.CityMap;
import pk.backend.domain.box.BoxValue;

import java.util.List;

public interface ControllerPort {
    List<CityMap> getMaps(List<Class<? extends BoxValue>> mapTypes);
    CityMap getFilteredMap(MapFilter mapFilter);
}