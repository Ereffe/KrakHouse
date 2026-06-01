package pk.backend.infrastructure.service.parser;

import org.springframework.stereotype.Component;
import pk.backend.infrastructure.dto.rcn.RcnLocalDto;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@Component
public class RcnLocalParser implements RcnObjectParser<RcnLocalDto> {

    @Override
    public boolean supports(String localName) {
        return "RCN_Lokal".equals(localName);
    }

    @Override
    public RcnLocalDto parse(XMLStreamReader reader) throws XMLStreamException {
        String gmlId = RcnStaxUtils.gmlId(reader);
        RcnStaxUtils.ParsedObject parsed = RcnStaxUtils.readObject(reader);
        RcnGeometryUtils.GeometryCenter center = RcnGeometryUtils.centerFromGeometry(parsed.geometryText())
                .orElse(null);

        return new RcnLocalDto(
                gmlId,
                RcnStaxUtils.value(parsed.values(), "idLokalu"),
                RcnStaxUtils.value(parsed.values(), "funkcjaLokalu"),
                RcnStaxUtils.integer(RcnStaxUtils.value(parsed.values(), "liczbaIzb")),
                RcnStaxUtils.integer(RcnStaxUtils.value(parsed.values(), "nrKondygnacji")),
                RcnStaxUtils.decimal(RcnStaxUtils.value(parsed.values(), "powUzytkowaLokalu")),
                RcnStaxUtils.decimal(RcnStaxUtils.value(parsed.values(), "cenaLokaluBrutto")),
                RcnStaxUtils.ref(parsed.refs(), "adresBudynkuZLokalem"),
                parsed.geometryText(),
                center == null ? null : center.centerX(),
                center == null ? null : center.centerY(),
                center == null ? null : center.srid()
        );
    }
}
