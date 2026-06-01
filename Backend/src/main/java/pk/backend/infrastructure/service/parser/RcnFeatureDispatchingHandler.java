package pk.backend.infrastructure.service.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pk.backend.infrastructure.dto.rcn.RcnObjectDto;
import pk.backend.infrastructure.service.RcnJdbcBatchWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RcnFeatureDispatchingHandler implements GmlFeatureHandler {

    private final List<RcnObjectParser<? extends RcnObjectDto>> parsers;
    private final RcnJdbcBatchWriter batchWriter;

    @Override
    public boolean supports(String localName) {
        return parsers.stream().anyMatch(parser -> parser.supports(localName));
    }

    @Override
    public void handle(String localName, XMLStreamReader reader) throws XMLStreamException {
        for (RcnObjectParser<? extends RcnObjectDto> parser : parsers) {
            if (parser.supports(localName)) {
                RcnObjectDto dto = parser.parse(reader);
                batchWriter.accept(dto);
                log.debug("Parsed RCN object: type={}, gmlId={}", localName, dto.gmlId());
                return;
            }
        }

        throw new XMLStreamException("No RCN parser found for " + localName);
    }

    @Override
    public void flush() {
        batchWriter.flush();
    }
}
