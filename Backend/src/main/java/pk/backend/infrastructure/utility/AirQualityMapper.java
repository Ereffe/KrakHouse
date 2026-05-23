package pk.backend.infrastructure.utility;

import lombok.experimental.UtilityClass;
import pk.backend.infrastructure.model.AirPollutionSensorsData;
import pk.backend.infrastructure.model.AirQualityData;

import java.util.List;

@UtilityClass
public class AirQualityMapper {

    public List<AirQualityData> mapToAQIList(List<AirPollutionSensorsData> airPollutionList){
        return airPollutionList.stream()
                .map(AirQualityMapper::mapToAQI)
                .toList();
    }

    public static AirQualityData mapToAQI(AirPollutionSensorsData airPollution){
        return AirQualityData.builder()
                .latitude(airPollution.getLatitude())
                .longitude(airPollution.getLongitude())
                .aqi(airPollution.getAqi())
                .build();
    }
}
