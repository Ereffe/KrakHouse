package pk.backend.domain.model.Exceptions;

public class BoxValueOutOfRangeException extends RuntimeException {
    public BoxValueOutOfRangeException(String min, String max) {
        super("Box value must be between %s and %s".formatted(min, max));
    }
}
