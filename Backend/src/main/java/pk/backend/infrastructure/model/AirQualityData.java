package pk.backend.infrastructure.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AirQualityData {

    private Double longitude;
    private Double latitude;
    private int aqi;
}
