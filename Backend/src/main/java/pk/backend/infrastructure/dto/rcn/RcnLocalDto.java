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
        String addressRef,
        String geometryText,
        Double centerX,
        Double centerY,
        Integer srid
) implements RcnObjectDto {
}
