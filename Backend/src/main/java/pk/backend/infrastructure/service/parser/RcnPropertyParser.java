package pk.backend.infrastructure.service.parser;

import org.springframework.stereotype.Component;
import pk.backend.infrastructure.dto.rcn.RcnPropertyDto;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@Component
public class RcnPropertyParser implements RcnObjectParser<RcnPropertyDto> {

    @Override
    public boolean supports(String localName) {
        return "RCN_Nieruchomosc".equals(localName);
    }

    @Override
    public RcnPropertyDto parse(XMLStreamReader reader) throws XMLStreamException {
        String gmlId = RcnStaxUtils.gmlId(reader);
        RcnStaxUtils.ParsedObject parsed = RcnStaxUtils.readObject(reader);

        return new RcnPropertyDto(
                gmlId,
                RcnStaxUtils.value(parsed.values(), "rodzajNieruchomosci"),
                RcnStaxUtils.value(parsed.values(), "rodzajPrawaDoNieruchomosci"),
                RcnStaxUtils.value(parsed.values(), "udzialWPrawieDoNieruchomosci"),
                RcnStaxUtils.decimal(RcnStaxUtils.value(parsed.values(), "cenaNieruchomosciBrutto")),
                RcnStaxUtils.refs(parsed.refs(), "dzialka"),
                RcnStaxUtils.refs(parsed.refs(), "budynek"),
                RcnStaxUtils.refs(parsed.refs(), "lokal")
        );
    }
}
