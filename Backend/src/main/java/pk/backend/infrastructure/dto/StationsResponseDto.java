package pk.backend.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StationsResponseDto(

        @JsonProperty("Lista stacji pomiarowych")
        List<StationsRecordDto> stations

) {
}
