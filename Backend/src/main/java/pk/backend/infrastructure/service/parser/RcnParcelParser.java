package pk.backend.infrastructure.service.parser;

import org.springframework.stereotype.Component;
import pk.backend.infrastructure.dto.rcn.RcnParcelDto;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@Component
public class RcnParcelParser implements RcnObjectParser<RcnParcelDto> {

    @Override
    public boolean supports(String localName) {
        return "RCN_Dzialka".equals(localName);
    }

    @Override
    public RcnParcelDto parse(XMLStreamReader reader) throws XMLStreamException {
        String gmlId = RcnStaxUtils.gmlId(reader);
        RcnStaxUtils.ParsedObject parsed = RcnStaxUtils.readObject(reader);

        return new RcnParcelDto(
                gmlId,
                RcnStaxUtils.value(parsed.values(), "idDzialki"),
                RcnStaxUtils.value(parsed.values(), "przeznaczenieWMPZP"),
                RcnStaxUtils.decimal(RcnStaxUtils.value(parsed.values(), "polePowierzchniEwidencyjnej")),
                RcnStaxUtils.value(parsed.values(), "sposobUzytkowania"),
                RcnStaxUtils.refs(parsed.refs(), "adresDzialki"),
                parsed.geometryText()
        );
    }
}
