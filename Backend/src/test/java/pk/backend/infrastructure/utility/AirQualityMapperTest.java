package pk.backend.infrastructure.utility;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pk.backend.infrastructure.dto.SingleSensorReadDto;
import pk.backend.infrastructure.model.AirPollutionSensorsData;
import pk.backend.infrastructure.model.DiscreteData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AirQualityMapperTest {

    @Test
    @DisplayName("Should correctly map AirPollutionSensorsData to DiscreteData")
    void shouldMapToAQI() {
        AirPollutionSensorsData pollutionData = new AirPollutionSensorsData();
        pollutionData.setLatitude(50.0);
        pollutionData.setLongitude(20.0);
        // PM2.5: 15.0 ug/m3 -> AQI 57
        pollutionData.addSensor("PM2.5", List.of(new SingleSensorReadDto(null, 15.0)));

        DiscreteData result = AirQualityMapper.mapToAQI(pollutionData);

        assertEquals(50.0, result.getLatitude());
        assertEquals(20.0, result.getLongitude());
        assertEquals(57, result.getValue());
    }

    @Test
    @DisplayName("Should correctly map a list of AirPollutionSensorsData")
    void shouldMapToAQIList() {
        AirPollutionSensorsData p1 = new AirPollutionSensorsData();
        p1.setLatitude(50.0);
        p1.setLongitude(20.0);
        p1.addSensor("PM2.5", List.of(new SingleSensorReadDto(null, 15.0))); // AQI 57

        AirPollutionSensorsData p2 = new AirPollutionSensorsData();
        p2.setLatitude(51.0);
        p2.setLongitude(21.0);
        p2.addSensor("PM10", List.of(new SingleSensorReadDto(null, 50.0))); // AQI 46

        List<DiscreteData> resultList = AirQualityMapper.mapToAQIList(List.of(p1, p2));

        assertEquals(2, resultList.size());
        assertEquals(57, resultList.get(0).getValue());
        assertEquals(46, resultList.get(1).getValue());
    }
}
