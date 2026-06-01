package pk.backend.infrastructure.dto.rcn;

import java.math.BigDecimal;
import java.util.List;

public record RcnParcelDto(
        String gmlId,
        String parcelId,
        String zoning,
        BigDecimal registryAreaHa,
        String usageType,
        List<String> addressRefs,
        String geometryText,
        Double centerX,
        Double centerY,
        Integer srid
) implements RcnObjectDto {
}
