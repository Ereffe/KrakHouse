package pk.backend.domain.model.Exceptions;

public class BoxObjectMismatchException extends RuntimeException {
    public BoxObjectMismatchException(String expected, String actual) {
        super("Cannot compare %s with %s".formatted(expected, actual));    }
}
