package pk.backend.infrastructure.dto.rcn;

import java.math.BigDecimal;

public record RcnTransactionDto(
        String gmlId,
        String transactionCode,
        String transactionType,
        String marketType,
        BigDecimal grossPrice,
        String propertyRef
) implements RcnObjectDto {
}