package pk.backend.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import pk.backend.domain.model.utils.CompareCondition;

public record FilteredMapDto(
        @NotBlank(message = "mapFilter cannot be blank")
        String mapFilter,

        @Positive(message = "value must be positive")
        float value,
        
        @NotNull(message = "condition cannot be null")
        CompareCondition condition
) {
}
