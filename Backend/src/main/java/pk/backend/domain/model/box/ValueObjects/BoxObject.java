package pk.backend.domain.model.box.ValueObjects;

public interface BoxObject {
    Number rawValue();
    int compareTo(BoxObject o);
}
