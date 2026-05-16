package pk.backend.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SensorDto(
        @JsonProperty("Identyfikator stanowiska")
        Long sensorId,

        @JsonProperty("Wskaźnik - wzór")
        String Indicator) {
}
