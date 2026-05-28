package pk.backend.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pk.backend.aplication.port.inbound.ControllerPort;
import pk.backend.infrastructure.dto.FilterResponseDto;
import pk.backend.infrastructure.dto.FilteredMapDto;
import pk.backend.infrastructure.dto.FilteredMapListResponseDto;
import pk.backend.infrastructure.dto.MergedMapResponseDto;
import pk.backend.infrastructure.utility.ResponseMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MapController {

    private final ControllerPort controllerPort;

    @GetMapping("/filters")
    public ResponseEntity<List<FilterResponseDto>> getFilters() {
        return ResponseEntity.ok(controllerPort.getFilters());
    }

    @GetMapping("/maps")
    public ResponseEntity<MergedMapResponseDto> getMergedMaps(@RequestBody List<FilteredMapDto> filteredMaps){
        var map = controllerPort.getMergedMaps(filteredMaps);
        return ResponseEntity.ok(ResponseMapper.mapToMergedDto(map, filteredMaps));
    }

    @GetMapping("/maps-list")
    public ResponseEntity<FilteredMapListResponseDto> getFilteredMap(@RequestBody List<FilteredMapDto> filteredMaps){
        var maps = controllerPort.getFilteredMap(filteredMaps);
        return ResponseEntity.ok(ResponseMapper.mapToFilteredListDto(maps, filteredMaps));
    }
}
