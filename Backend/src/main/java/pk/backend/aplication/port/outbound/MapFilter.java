package pk.backend.aplication.port.outbound;

import pk.backend.domain.box.BoxValue;
import pk.backend.domain.utils.CompareCondition;

public record MapFilter(
        BoxValue value,
        CompareCondition condition
) {
}
