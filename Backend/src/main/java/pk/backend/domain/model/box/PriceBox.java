package pk.backend.domain.model.box;


import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.domain.model.box.ValueObjects.PriceBoxObject;

import java.math.BigDecimal;

public class PriceBox implements BoxValue {

    private PriceBoxObject value;

    public PriceBox(BigDecimal value){
        this.value = new PriceBoxObject(value);
    }

    @Override
    public PriceBoxObject getValue() {
        return value;
    }

    @Override
    public int compareTo(BoxValue other) {
        return value.compareTo(((PriceBox)other).value);
    }
}
