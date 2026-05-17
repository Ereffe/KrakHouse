package pk.backend.domain.model.box.ValueObjects;

public interface BoxValue {
//    TODO: 2 create value-object for Value
    BoxObject getValue();
    int compareTo(BoxValue other);
}
