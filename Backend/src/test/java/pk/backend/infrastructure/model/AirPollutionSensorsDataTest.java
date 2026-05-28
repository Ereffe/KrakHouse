package pk.backend.infrastructure.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pk.backend.infrastructure.dto.SingleSensorReadDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AirPollutionSensorsDataTest {

    @Test
    @DisplayName("Should return 0 when no sensors are present")
    void shouldReturnZeroWhenNoSensors() {
        AirPollutionSensorsData data = new AirPollutionSensorsData();
        assertEquals(0, data.getAqi());
    }

    @Test
    @DisplayName("Should calculate correct AQI for PM2.5 (Moderate range)")
    void shouldCalculateCorrectAQIForPM25() {
        AirPollutionSensorsData data = new AirPollutionSensorsData();
        // 15.0 ug/m3 PM2.5 -> range [12.1, 35.4] -> AQI [51, 100]
        data.addSensor("PM2.5", List.of(new SingleSensorReadDto(null, 15.0)));
        
        assertEquals(57, data.getAqi());
    }

    @Test
    @DisplayName("Should handle missing O3 and calculate AQI based on other sensors")
    void shouldHandleMissingO3() {
        AirPollutionSensorsData data = new AirPollutionSensorsData();
        // PM10: 50.0 ug/m3 -> range [0, 54.0] -> AQI [0, 50]
        data.addSensor("PM10", List.of(new SingleSensorReadDto(null, 50.0)));
        
        assertEquals(46, data.getAqi());
    }

    @Test
    @DisplayName("Should pick max AQI when one pollutant is very high")
    void shouldPickMaxAQI() {
        AirPollutionSensorsData data = new AirPollutionSensorsData();
        
        // PM2.5: 10.0 ug/m3 -> AQI roughly 42
        data.addSensor("PM2.5", List.of(new SingleSensorReadDto(null, 10.0)));
        
        // NO2: 200.0 ug/m3 -> converted to ppb: 200 * 0.5315 = 106.3 ppb
        data.addSensor("NO2", List.of(new SingleSensorReadDto(null, 200.0)));
        
        assertEquals(102, data.getAqi());
    }

    @Test
    @DisplayName("Should handle sensors with null values correctly")
    void shouldHandleNullValues() {
        AirPollutionSensorsData data = new AirPollutionSensorsData();
        data.addSensor("PM2.5", List.of(
            new SingleSensorReadDto(null, null),
            new SingleSensorReadDto(null, 20.0)
        ));
        
        // Avg = 20.0 -> range [12.1, 35.4] -> AQI [51, 100]
        assertEquals(68, data.getAqi());
    }
}
