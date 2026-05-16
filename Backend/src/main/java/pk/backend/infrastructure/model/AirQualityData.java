package pk.backend.infrastructure.model;

import lombok.Getter;
import lombok.Setter;
import pk.backend.infrastructure.dto.SingleSensorReadDto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AirQualityData {

    private Long id;
    private Double longitude;
    private Double latitude;
    private Map<String,Double> sensors = new HashMap<>();

    public void addSensor(String sensorName, List<SingleSensorReadDto> sensorValues) {
//        TODO: aggregate data of a single day
        double avg = 0.0;
        int readCount = sensorValues.size();
        for(var sensorValue : sensorValues) {
            if(sensorValue.value() == null){
                readCount--;
                continue;
            }
            avg += sensorValue.value();
        }
        avg /= readCount;
        sensors.put(sensorName, avg);
    }

    public int getAQI(){
//        TODO: implement method
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
