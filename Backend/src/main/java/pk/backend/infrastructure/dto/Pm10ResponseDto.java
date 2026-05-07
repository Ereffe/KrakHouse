package pk.backend.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Pm10ResponseDto(

        @JsonProperty("Lista danych zagregowanych")
        List<Pm10RecordDto> pm10RecordDtoList

        ) {
}
