package pk.backend.domain.model.box;

import pk.backend.domain.model.box.ValueObjects.AirQualityBoxObject;
import pk.backend.domain.model.box.ValueObjects.BoxValue;

public class AirQualityBox implements BoxValue {

    private AirQualityBoxObject value;

    public AirQualityBox(int value) {
        this.value = new AirQualityBoxObject(value);
    }

    @Override
    public AirQualityBoxObject getValue() {
        return value;
    }

    @Override
    public int compareTo(BoxValue other) {
        return value.compareTo(other.getValue());
    }
}
