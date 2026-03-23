package pk.backend.domain.box;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@AllArgsConstructor
public class PriceBox implements BoxValue {

    private BigDecimal value;

    @Override
    public Object getValue() {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int compareTo(BoxValue other) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
