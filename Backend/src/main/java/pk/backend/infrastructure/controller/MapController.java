package pk.backend.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pk.backend.aplication.port.outbound.MapFilter;
import pk.backend.domain.CityMap.CityMap;
import pk.backend.domain.utils.CompareCondition;
import pk.backend.infrastructure.MapType;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MapController {

    @GetMapping("/maps")
    public ResponseEntity<List<CityMap>> getMaps(@RequestParam List<MapType> mapType){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping("/filtered-maps")
    public ResponseEntity<CityMap> getFilteredMap(@RequestParam float value, @RequestParam CompareCondition condition){
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
