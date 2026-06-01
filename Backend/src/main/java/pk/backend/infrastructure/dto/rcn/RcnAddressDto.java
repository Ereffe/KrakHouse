package pk.backend.infrastructure.dto.rcn;

public record RcnAddressDto(
        String gmlId,
        String city,
        String street,
        String buildingNumber
) implements RcnObjectDto {
}