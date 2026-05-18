package pk.backend.domain.model.box.ValueObjects;

import pk.backend.domain.model.Exceptions.InvalidBoxValueException;

import java.math.BigDecimal;

public class PriceBoxObject extends AbstactBoxObject<BigDecimal>{
    public PriceBoxObject(BigDecimal value) {
        super(validate(value));
    }

    private static BigDecimal validate(BigDecimal value) {
        if(value == null){
            throw new InvalidBoxValueException("Price Box Value is null");
        }
        if(value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidBoxValueException("Price cannot be negative");
        }
        return value.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
