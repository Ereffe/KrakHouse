package pk.backend.domain.model.box.ValueObjects;

import pk.backend.domain.model.Exceptions.BoxValueOutOfRangeException;

public class CrimeBoxObject extends AbstactBoxObject<Float>{

    public CrimeBoxObject(float value){
        super(validate(value));
    }

    private static float validate(float value) {
        if(value > 1 || value < 0) {
            throw new BoxValueOutOfRangeException("0%","100%");
        }
        return value;
    }
}
