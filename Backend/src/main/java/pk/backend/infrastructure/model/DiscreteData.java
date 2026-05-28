package pk.backend.infrastructure.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiscreteData<T extends Number & Comparable<T>> {

    private Double longitude;
    private Double latitude;
    private T value;
}
