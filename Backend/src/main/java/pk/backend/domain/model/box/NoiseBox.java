package pk.backend.domain.model.box;


import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.domain.model.box.ValueObjects.NoiseBoxObject;

public class NoiseBox implements BoxValue {

    private NoiseBoxObject value;

    public NoiseBox(int value){
        this.value = new NoiseBoxObject(value);
    }

    @Override
    public NoiseBoxObject getValue() {
        return value;
    }

    @Override
    public int compareTo(BoxValue other) {
        return value.compareTo(other.getValue());
    }
}
