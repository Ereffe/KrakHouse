package pk.backend.infrastructure.model;

import lombok.Getter;
import lombok.Setter;
import pk.backend.infrastructure.dto.SingleSensorReadDto;
import pk.backend.infrastructure.utility.AQIParameters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AirPollutionSensorsData {

    private Long id;
    private Double longitude;
    private Double latitude;
    private Map<String,Double> sensors = new HashMap<>();

    public void addSensor(String sensorName, List<SingleSensorReadDto> sensorValues) {
        double avg = 0.0;
        int readCount = sensorValues.size();
        for(var sensorValue : sensorValues) {
            if(sensorValue.value() == null){
                readCount--;
                continue;
            }
            avg += sensorValue.value();
        }
        if (readCount > 0) {
            avg /= readCount;
        }
        sensors.put(sensorName, avg);
    }

    public int getAqi(){
        if (sensors.isEmpty()) {
            return 0;
        }

        Map<String, double[]> allParams = AQIParameters.getParamsForSensors(sensors);
        int maxAQI = 0;

        for (double[] params : allParams.values()) {
            double bpLo = params[0];
            double bpHi = params[1];
            int iLo = (int) params[2];
            int iHi = (int) params[3];
            double value = params[4];

            if (bpHi == bpLo) {
                maxAQI = Math.max(maxAQI, iHi);
                continue;
            }

            int aqi = (int) Math.round(((double)(iHi - iLo) / (bpHi - bpLo)) * (value - bpLo) + iLo);
            maxAQI = Math.max(maxAQI, aqi);
        }

        return maxAQI;
    }
}
