package pk.backend.infrastructure.service.parser;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.math.BigDecimal;
import java.util.*;

final class RcnStaxUtils {

    private static final Set<String> GML_NAMESPACES = Set.of(
            "http://www.opengis.net/gml",
            "http://www.opengis.net/gml/3.2"
    );

    private static final String XLINK_NAMESPACE = "http://www.w3.org/1999/xlink";

    private RcnStaxUtils() {
    }

    static String gmlId(XMLStreamReader reader) {
        for (String namespace : GML_NAMESPACES) {
            String id = reader.getAttributeValue(namespace, "id");
            if (id != null) {
                return id;
            }
        }

        return reader.getAttributeValue(null, "id");
    }

    static ParsedObject readObject(XMLStreamReader reader) throws XMLStreamException {
        int depth = 1;
        String currentElement = null;
        boolean insideGeometry = false;

        Map<String, String> values = new HashMap<>();
        Map<String, List<String>> refs = new HashMap<>();
        StringBuilder geometry = new StringBuilder();

        while (reader.hasNext() && depth > 0) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
                currentElement = reader.getLocalName();

                String href = reader.getAttributeValue(XLINK_NAMESPACE, "href");
                if (href != null) {
                    refs.computeIfAbsent(currentElement, key -> new ArrayList<>()).add(normalizeHref(href));
                }

                if ("geometria".equals(currentElement) || "georeferencja".equals(currentElement)) {
                    insideGeometry = true;
                }

                if (insideGeometry) {
                    appendStartElement(geometry, reader);
                }
            } else if (event == XMLStreamConstants.CHARACTERS || event == XMLStreamConstants.CDATA) {
                String text = reader.getText();

                if (insideGeometry) {
                    geometry.append(text);
                } else if (currentElement != null && text != null && !text.isBlank()) {
                    values.put(currentElement, text.trim());
                }
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (insideGeometry) {
                    appendEndElement(geometry, reader);
                }

                if ("geometria".equals(reader.getLocalName()) || "georeferencja".equals(reader.getLocalName())) {
                    insideGeometry = false;
                }

                depth--;
                currentElement = null;
            }
        }

        return new ParsedObject(values, refs, geometry.toString());
    }

    static String value(Map<String, String> values, String name) {
        String value = values.get(name);
        return value == null || value.isBlank() ? null : value;
    }

    static String ref(Map<String, List<String>> refs, String name) {
        List<String> values = refs.get(name);
        return values == null || values.isEmpty() ? null : values.getFirst();
    }

    static List<String> refs(Map<String, List<String>> refs, String name) {
        return refs.getOrDefault(name, List.of());
    }

    static BigDecimal decimal(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return new BigDecimal(value.replace(",", "."));
    }

    static Integer integer(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Integer.valueOf(value);
    }

    private static String normalizeHref(String href) {
        return href.startsWith("#") ? href.substring(1) : href;
    }

    private static void appendStartElement(StringBuilder target, XMLStreamReader reader) {
        target.append("<").append(qualifiedName(reader.getPrefix(), reader.getLocalName()));

        for (int i = 0; i < reader.getNamespaceCount(); i++) {
            String prefix = reader.getNamespacePrefix(i);
            String namespaceUri = reader.getNamespaceURI(i);

            target.append(" ");
            if (prefix == null || prefix.isBlank()) {
                target.append("xmlns");
            } else {
                target.append("xmlns:").append(prefix);
            }
            target.append("=\"").append(escapeXml(namespaceUri)).append("\"");
        }

        for (int i = 0; i < reader.getAttributeCount(); i++) {
            target.append(" ")
                    .append(qualifiedName(reader.getAttributePrefix(i), reader.getAttributeLocalName(i)))
                    .append("=\"")
                    .append(escapeXml(reader.getAttributeValue(i)))
                    .append("\"");
        }

        target.append(">");
    }

    private static void appendEndElement(StringBuilder target, XMLStreamReader reader) {
        target.append("</")
                .append(qualifiedName(reader.getPrefix(), reader.getLocalName()))
                .append(">");
    }

    private static String qualifiedName(String prefix, String localName) {
        if (prefix == null || prefix.isBlank()) {
            return localName;
        }

        return prefix + ":" + localName;
    }

    private static String escapeXml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    record ParsedObject(
            Map<String, String> values,
            Map<String, List<String>> refs,
            String geometryText
    ) {
    }
}
