package pk.backend.aplication.port.inbound;

import org.jspecify.annotations.Nullable;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.infrastructure.dto.FilteredMapDto;

import java.util.List;

public interface ControllerPort {


    List<String> getFilters();


    CityMap getMergedMaps( List<FilteredMapDto> filteredMaps);


    List<CityMap> getFilteredMap( List<FilteredMapDto> filteredMaps);
}