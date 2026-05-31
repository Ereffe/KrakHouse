package pk.backend.infrastructure.service.parser;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public interface GmlFeatureHandler {

    boolean supports(String localName);

    void handle(String localName, XMLStreamReader reader) throws XMLStreamException;
}