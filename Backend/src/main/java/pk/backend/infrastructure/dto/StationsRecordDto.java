package pk.backend.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StationsRecordDto(

        @JsonProperty("Identyfikator stacji")
        Long id,

        @JsonProperty("WGS84 λ E")
        Double longitude,

        @JsonProperty("WGS84 φ N")
        Double latitude,

        @JsonProperty("Nazwa miasta")
        String cityName

) {
}
