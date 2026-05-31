package pk.backend.infrastructure.dto.rcn;

public record RcnBuildingDto(
        String gmlId,
        String buildingId,
        String buildingType,
        String addressRef,
        String geometryText
) implements RcnObjectDto {
}