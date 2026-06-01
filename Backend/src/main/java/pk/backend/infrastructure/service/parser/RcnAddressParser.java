package pk.backend.infrastructure.service.parser;

import org.springframework.stereotype.Component;
import pk.backend.infrastructure.dto.rcn.RcnAddressDto;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@Component
public class RcnAddressParser implements RcnObjectParser<RcnAddressDto> {

    @Override
    public boolean supports(String localName) {
        return "RCN_Adres".equals(localName);
    }

    @Override
    public RcnAddressDto parse(XMLStreamReader reader) throws XMLStreamException {
        String gmlId = RcnStaxUtils.gmlId(reader);
        RcnStaxUtils.ParsedObject parsed = RcnStaxUtils.readObject(reader);

        return new RcnAddressDto(
                gmlId,
                RcnStaxUtils.value(parsed.values(), "miejscowosc"),
                RcnStaxUtils.value(parsed.values(), "ulica"),
                RcnStaxUtils.value(parsed.values(), "numerPorzadkowy")
        );
    }
}
