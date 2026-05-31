package pk.backend.infrastructure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pk.backend.infrastructure.service.parser.GmlFeatureHandler;
import pk.backend.infrastructure.service.parser.GmlParseResult;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmlStreamParser {

    private static final String FEATURE_MEMBER = "featureMember";

    private static final Set<String> GML_NAMESPACES = Set.of(
            "http://www.opengis.net/gml",
            "http://www.opengis.net/gml/3.2"
    );

    private final List<GmlFeatureHandler> handlers;

    public GmlParseResult parse(Path file) {
        XMLInputFactory factory = secureXmlInputFactory();

        long featureMemberCount = 0;
        long handledObjectCount = 0;
        long skippedObjectCount = 0;

        try (InputStream inputStream = Files.newInputStream(file)) {
            XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

            try {
                while (reader.hasNext()) {
                    int event = reader.next();

                    if (event == XMLStreamConstants.START_ELEMENT && isFeatureMember(reader)) {
                        featureMemberCount++;

                        boolean handled = parseFeatureMember(reader);

                        if (handled) {
                            handledObjectCount++;
                        } else {
                            skippedObjectCount++;
                        }

                        if (featureMemberCount % 10_000 == 0) {
                            log.info("Parsed {} gml:featureMember elements from {}", featureMemberCount, file);
                        }
                    }
                }
            } finally {
                reader.close();
            }

            log.info(
                    "Finished parsing GML file: file={}, featureMembers={}, handled={}, skipped={}",
                    file,
                    featureMemberCount,
                    handledObjectCount,
                    skippedObjectCount
            );

            return new GmlParseResult(featureMemberCount, handledObjectCount, skippedObjectCount);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read GML file: " + file, e);
        } catch (XMLStreamException e) {
            throw new IllegalStateException("Could not parse GML file: " + file, e);
        }
    }

    private boolean parseFeatureMember(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                String localName = reader.getLocalName();

                GmlFeatureHandler handler = findHandler(localName);

                if (handler == null) {
                    log.debug("Skipping unsupported GML feature object: {}", localName);
                    skipSubtree(reader);
                    return false;
                }

                handler.handle(localName, reader);
                return true;
            }

            if (event == XMLStreamConstants.END_ELEMENT && isFeatureMember(reader)) {
                return false;
            }
        }

        return false;
    }

    private GmlFeatureHandler findHandler(String localName) {
        for (GmlFeatureHandler handler : handlers) {
            if (handler.supports(localName)) {
                return handler;
            }
        }

        return null;
    }

    private boolean isFeatureMember(XMLStreamReader reader) {
        String namespace = reader.getNamespaceURI();

        return FEATURE_MEMBER.equals(reader.getLocalName())
                && (namespace == null || GML_NAMESPACES.contains(namespace));
    }

    private void skipSubtree(XMLStreamReader reader) throws XMLStreamException {
        int depth = 1;

        while (reader.hasNext() && depth > 0) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                depth--;
            }
        }
    }

    private XMLInputFactory secureXmlInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newFactory();

        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        factory.setProperty(XMLInputFactory.IS_COALESCING, true);

        return factory;
    }
}