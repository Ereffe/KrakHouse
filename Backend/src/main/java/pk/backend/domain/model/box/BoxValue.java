package pk.backend.domain.model.box;

public interface BoxValue {
//    TODO: 2 create value-object for Value
    Object getValue();
    int compareTo(BoxValue other);
}
