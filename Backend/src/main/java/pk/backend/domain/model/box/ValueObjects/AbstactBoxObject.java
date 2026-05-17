package pk.backend.domain.model.box.ValueObjects;

import pk.backend.domain.model.Exceptions.BoxObjectMismatchException;

public abstract class AbstactBoxObject <T extends Number & Comparable<T>> implements BoxObject {
    private final T value;

    protected AbstactBoxObject(T value) {
        this.value = value;
    }

    @Override
    public T rawValue() {
        return value;
    }

    @Override
    public int compareTo(BoxObject other) {
        if(other == null || !getClass().equals(other.getClass())) {
            throw new BoxObjectMismatchException(this.getClass().getSimpleName(),
                    other==null?"null":other.getClass().getSimpleName());
        }

        T otherVal = (T)other.rawValue();
        return value.compareTo(otherVal);
    }
}
