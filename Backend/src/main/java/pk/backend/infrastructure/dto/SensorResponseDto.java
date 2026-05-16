package pk.backend.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SensorResponseDto(
        @JsonProperty("Lista stanowisk pomiarowych dla podanej stacji")
        List<SensorDto> sensors) {
}
