package pk.backend.domain.model.box.ValueObjects;

import pk.backend.domain.model.Exceptions.BoxValueOutOfRangeException;

public class NoiseBoxObject extends AbstactBoxObject<Integer> {
    public NoiseBoxObject(int value) {
        super(validate(value));
    }

    private static int validate(Integer value) {
        if(value > 200 || value < 0) {
            throw new BoxValueOutOfRangeException("0","200");
        }
        return value;
    }
}
