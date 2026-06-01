package pk.backend.infrastructure.service.parser;

import org.springframework.stereotype.Component;
import pk.backend.infrastructure.dto.rcn.RcnTransactionDto;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@Component
public class RcnTransactionParser implements RcnObjectParser<RcnTransactionDto> {

    @Override
    public boolean supports(String localName) {
        return "RCN_Transakcja".equals(localName);
    }

    @Override
    public RcnTransactionDto parse(XMLStreamReader reader) throws XMLStreamException {
        String gmlId = RcnStaxUtils.gmlId(reader);
        RcnStaxUtils.ParsedObject parsed = RcnStaxUtils.readObject(reader);

        return new RcnTransactionDto(
                gmlId,
                RcnStaxUtils.value(parsed.values(), "oznaczenieTransakcji"),
                RcnStaxUtils.value(parsed.values(), "rodzajTransakcji"),
                RcnStaxUtils.value(parsed.values(), "rodzajRynku"),
                RcnStaxUtils.decimal(RcnStaxUtils.value(parsed.values(), "cenaTransakcjiBrutto")),
                RcnStaxUtils.ref(parsed.refs(), "nieruchomosc")
        );
    }
}
