package pk.backend.infrastructure.service.parser;

import org.springframework.stereotype.Component;
import pk.backend.infrastructure.dto.rcn.RcnBuildingDto;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@Component
public class RcnBuildingParser implements RcnObjectParser<RcnBuildingDto> {

    @Override
    public boolean supports(String localName) {
        return "RCN_Budynek".equals(localName);
    }

    @Override
    public RcnBuildingDto parse(XMLStreamReader reader) throws XMLStreamException {
        String gmlId = RcnStaxUtils.gmlId(reader);
        RcnStaxUtils.ParsedObject parsed = RcnStaxUtils.readObject(reader);

        return new RcnBuildingDto(
                gmlId,
                RcnStaxUtils.value(parsed.values(), "idBudynku"),
                RcnStaxUtils.value(parsed.values(), "rodzajBudynku"),
                RcnStaxUtils.ref(parsed.refs(), "adresBudynku"),
                parsed.geometryText()
        );
    }
}
