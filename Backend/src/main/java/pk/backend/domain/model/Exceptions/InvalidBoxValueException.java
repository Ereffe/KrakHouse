package pk.backend.domain.model.Exceptions;

public class InvalidBoxValueException extends RuntimeException {
  public InvalidBoxValueException(String message) {
    super(message);
  }
}
