package pk.backend.domain.model.box;

public interface BoxValue {
//    TODO: change return type
    Object getValue();
    int compareTo(BoxValue other);
}
