package pk.backend.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FilteredMapDto(
        @NotBlank(message = "mapFilter cannot be blank")
        String mapFilter,

        @NotNull(message = "minValue cannot be null")
        Float minValue,

        @NotNull(message = "maxValue cannot be null")
        Float maxValue
) {
}
