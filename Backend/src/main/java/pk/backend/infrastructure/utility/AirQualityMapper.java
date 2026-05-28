package pk.backend.infrastructure.utility;

import lombok.experimental.UtilityClass;
import pk.backend.infrastructure.model.AirPollutionSensorsData;
import pk.backend.infrastructure.model.DiscreteData;

import java.util.List;

@UtilityClass
public class AirQualityMapper {

    public List<DiscreteData<Integer>> mapToAQIList(List<AirPollutionSensorsData> airPollutionList){
        return airPollutionList.stream()
                .map(AirQualityMapper::mapToAQI)
                .toList();
    }

    public static DiscreteData<Integer> mapToAQI(AirPollutionSensorsData airPollution){
        return DiscreteData.<Integer>builder()
                .latitude(airPollution.getLatitude())
                .longitude(airPollution.getLongitude())
                .value(airPollution.getAqi())
                .build();
    }
}
