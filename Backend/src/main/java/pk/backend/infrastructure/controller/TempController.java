package pk.backend.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pk.backend.infrastructure.adapter.AirQualityMapAdapter;
import pk.backend.infrastructure.service.AirQualityService;

//FIXME: REMOVE AFTER TEST
@RestController
@RequiredArgsConstructor
public class TempController {

    private final AirQualityMapAdapter airQualityMapAdapter;

//    TODO: remove
    private final AirQualityService airQualityService;

    @GetMapping("/test")
    public void performTest(){
        airQualityMapAdapter.createMap();
//        airQualityService.requestSensorData(2745,0);
    }
}
