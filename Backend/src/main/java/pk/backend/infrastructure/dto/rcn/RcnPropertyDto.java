package pk.backend.infrastructure.dto.rcn;

import java.math.BigDecimal;
import java.util.List;

public record RcnPropertyDto(
        String gmlId,
        String propertyType,
        String propertyRightType,
        String propertyRightShare,
        BigDecimal grossPrice,
        List<String> parcelRefs,
        List<String> buildingRefs,
        List<String> localRefs
) implements RcnObjectDto {
}