package pk.backend.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.infrastructure.dto.FilteredMapDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MapController {

    @GetMapping("/filters")
    public ResponseEntity<List<String>> getFilters() {
        //    TODO: 1 implement controller flow
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/maps")
    public ResponseEntity<List<CityMap>> getMaps(@RequestParam List<String> mapType){
        //    TODO: 1 implement controller flow
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/filtered-maps")
    public ResponseEntity<CityMap> getFilteredMap(@RequestParam List<FilteredMapDto> filteredMaps){
        //    TODO: 1 implement controller flow
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
