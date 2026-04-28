package pk.backend.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pk.backend.domain.CityMap.CityMap;
import pk.backend.domain.utils.Filter;
import pk.backend.infrastructure.dto.FilteredMapDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MapController {

    @GetMapping("/filters")
    public ResponseEntity<List<Filter>> getFilters() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/maps")
    public ResponseEntity<List<CityMap>> getMaps(@RequestParam List<Filter> mapType){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/filtered-maps")
    public ResponseEntity<CityMap> getFilteredMap(@RequestParam List<FilteredMapDto> filteredMaps){
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
