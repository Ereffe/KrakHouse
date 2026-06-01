package pk.backend.infrastructure.service.parser;

import pk.backend.infrastructure.dto.rcn.RcnObjectDto;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public interface RcnObjectParser<T extends RcnObjectDto> {

    boolean supports(String localName);

    T parse(XMLStreamReader reader) throws XMLStreamException;
}
