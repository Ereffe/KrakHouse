package pk.backend.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SensorDataResponseDto(
        @JsonProperty("Lista archiwalnych wyników pomiarów")
        List<SingleSensorReadDto> readSequence) {
}
