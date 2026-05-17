package pk.backend.domain.model.box;

import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.domain.model.box.ValueObjects.CrimeBoxObject;

public class CrimeBox implements BoxValue {

    private CrimeBoxObject value;

    public CrimeBox(float value){
        this.value = new CrimeBoxObject(value);
    }

    @Override
    public CrimeBoxObject getValue() {
        return value;
    }

    @Override
    public int compareTo(BoxValue other) {
        return value.compareTo(other.getValue());
    }
}
