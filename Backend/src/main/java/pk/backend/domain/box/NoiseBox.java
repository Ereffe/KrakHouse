package pk.backend.domain.box;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoiseBox implements BoxValue {

    private int value;

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public int compareTo(BoxValue other) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
