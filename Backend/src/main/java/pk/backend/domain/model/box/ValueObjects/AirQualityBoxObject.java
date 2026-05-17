package pk.backend.domain.model.box.ValueObjects;

import pk.backend.domain.model.Exceptions.BoxValueOutOfRangeException;
import pk.backend.domain.model.Exceptions.InvalidBoxValueException;

public class AirQualityBoxObject extends AbstactBoxObject<Integer> {

    public AirQualityBoxObject(Integer value) {
        super(validate(value));
    }

    private static Integer validate(Integer value){
        if(value == null) throw new InvalidBoxValueException("Air Quality Box Value is null");
        if(value > 500 || value < 0) throw new BoxValueOutOfRangeException("0","500");
        return value;
    }
}
