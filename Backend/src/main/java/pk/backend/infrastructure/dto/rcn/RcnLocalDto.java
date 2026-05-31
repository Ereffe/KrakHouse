package pk.backend.infrastructure.dto.rcn;

import java.math.BigDecimal;

public record RcnLocalDto(
        String gmlId,
        String localId,
        String localFunction,
        Integer roomCount,
        Integer floorNumber,
        BigDecimal usableArea,
        BigDecimal grossPrice,
        String addressRef
) implements RcnObjectDto {
}