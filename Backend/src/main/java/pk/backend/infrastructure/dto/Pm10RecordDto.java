package pk.backend.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Pm10RecordDto(

        @JsonProperty("Kod stanowiska")
        String positionCode,

        @JsonProperty("Data")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime date,

        @JsonProperty("Maksimum ze średnich 8-godzinnych")
        Double maxValue,

        @JsonProperty("Średnia 24-godzinna z wyników 1-godzinnych")
        Double averageValue
) {
}
