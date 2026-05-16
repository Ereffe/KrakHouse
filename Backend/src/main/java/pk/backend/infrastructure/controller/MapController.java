package pk.backend.infrastructure.controller;

// lombok constructor removed to ensure explicit constructor for static analysis
import pk.backend.aplication.port.inbound.ControllerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pk.backend.domain.model.CityMap.CityMap;
import pk.backend.infrastructure.dto.FilteredMapDto;

import java.util.List;

@RestController
public class MapController {

    private final ControllerPort controllerPort;
    
    public MapController(ControllerPort controllerPort) {
        this.controllerPort = controllerPort;
    }

    @GetMapping("/filters")
    public ResponseEntity<List<String>> getFilters() {
        return ResponseEntity.ok(controllerPort.getFilters());
    }

    @GetMapping("/maps")
    public ResponseEntity<CityMap> getMergedMaps(@RequestParam List<FilteredMapDto> filteredMaps){
        var map = controllerPort.getMergedMaps(filteredMaps);
        return ResponseEntity.ok(map);
    }

    @GetMapping("/maps-list")
    public ResponseEntity<List<CityMap>> getFilteredMap(@RequestParam List<FilteredMapDto> filteredMaps){
        var maps = controllerPort.getFilteredMap(filteredMaps);
        return ResponseEntity.ok(maps);
    }

}
