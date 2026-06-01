package pk.backend.infrastructure.service.parser;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class RcnGeometryUtils {

    private static final Pattern POSITION_ELEMENT = Pattern.compile(
            "<(?:\\w+:)?(posList|pos)\\b[^>]*>([^<]+)</(?:\\w+:)?\\1>",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SRID = Pattern.compile("EPSG::(\\d+)");

    private static final int GOOGLE_MAPS_SRID = 4326;
    private static final int PL_2000_ZONE_7_SRID = 2178;

    private RcnGeometryUtils() {
    }

    static Optional<GeometryCenter> centerFromGeometry(String geometryText) {
        if (geometryText == null || geometryText.isBlank()) {
            return Optional.empty();
        }

        Integer sourceSrid = sourceSrid(geometryText);
        BoundingBox boundingBox = boundingBox(geometryText);

        if (sourceSrid == null || boundingBox == null) {
            return Optional.empty();
        }

        double sourceCenterX = (boundingBox.minX() + boundingBox.maxX()) / 2.0;
        double sourceCenterY = (boundingBox.minY() + boundingBox.maxY()) / 2.0;

        if (sourceSrid == GOOGLE_MAPS_SRID) {
            return Optional.of(new GeometryCenter(sourceCenterX, sourceCenterY, GOOGLE_MAPS_SRID));
        }

        if (sourceSrid == PL_2000_ZONE_7_SRID) {
            Wgs84Coordinate coordinate = transformEpsg2178ToWgs84(sourceCenterX, sourceCenterY);
            return Optional.of(new GeometryCenter(coordinate.longitude(), coordinate.latitude(), GOOGLE_MAPS_SRID));
        }

        return Optional.empty();
    }

    private static Integer sourceSrid(String geometryText) {
        Matcher matcher = SRID.matcher(geometryText);
        if (!matcher.find()) {
            return null;
        }

        return Integer.valueOf(matcher.group(1));
    }

    private static BoundingBox boundingBox(String geometryText) {
        Matcher matcher = POSITION_ELEMENT.matcher(geometryText);
        BoundingBox box = null;

        while (matcher.find()) {
            String[] values = matcher.group(2).trim().split("\\s+");

            for (int i = 0; i + 1 < values.length; i += 2) {
                double x = Double.parseDouble(values[i]);
                double y = Double.parseDouble(values[i + 1]);
                box = box == null ? new BoundingBox(x, y, x, y) : box.include(x, y);
            }
        }

        return box;
    }

    /**
     * EPSG:2178 is Poland CS2000 zone 7. GML positions are northing/easting.
     * The returned coordinate uses Google Maps order: longitude, latitude.
     */
    private static Wgs84Coordinate transformEpsg2178ToWgs84(double northing, double easting) {
        double semiMajorAxis = 6378137.0;
        double inverseFlattening = 298.257222101;
        double flattening = 1.0 / inverseFlattening;
        double eccentricitySquared = flattening * (2.0 - flattening);
        double secondEccentricitySquared = eccentricitySquared / (1.0 - eccentricitySquared);
        double scaleFactor = 0.999923;
        double centralMeridian = Math.toRadians(21.0);
        double falseEasting = 7_500_000.0;

        double meridionalArc = northing / scaleFactor;
        double mu = meridionalArc / (semiMajorAxis
                * (1.0 - eccentricitySquared / 4.0
                - 3.0 * Math.pow(eccentricitySquared, 2) / 64.0
                - 5.0 * Math.pow(eccentricitySquared, 3) / 256.0));

        double e1 = (1.0 - Math.sqrt(1.0 - eccentricitySquared))
                / (1.0 + Math.sqrt(1.0 - eccentricitySquared));

        double footprintLatitude = mu
                + (3.0 * e1 / 2.0 - 27.0 * Math.pow(e1, 3) / 32.0) * Math.sin(2.0 * mu)
                + (21.0 * Math.pow(e1, 2) / 16.0 - 55.0 * Math.pow(e1, 4) / 32.0) * Math.sin(4.0 * mu)
                + (151.0 * Math.pow(e1, 3) / 96.0) * Math.sin(6.0 * mu)
                + (1097.0 * Math.pow(e1, 4) / 512.0) * Math.sin(8.0 * mu);

        double sinFootprint = Math.sin(footprintLatitude);
        double cosFootprint = Math.cos(footprintLatitude);
        double tanFootprint = Math.tan(footprintLatitude);

        double radiusPrimeVertical = semiMajorAxis / Math.sqrt(1.0 - eccentricitySquared * sinFootprint * sinFootprint);
        double radiusMeridian = semiMajorAxis * (1.0 - eccentricitySquared)
                / Math.pow(1.0 - eccentricitySquared * sinFootprint * sinFootprint, 1.5);
        double tangentSquared = tanFootprint * tanFootprint;
        double etaSquared = secondEccentricitySquared * cosFootprint * cosFootprint;
        double normalizedEasting = (easting - falseEasting) / (radiusPrimeVertical * scaleFactor);

        double latitude = footprintLatitude - (radiusPrimeVertical * tanFootprint / radiusMeridian)
                * (Math.pow(normalizedEasting, 2) / 2.0
                - (5.0 + 3.0 * tangentSquared + 10.0 * etaSquared
                - 4.0 * etaSquared * etaSquared - 9.0 * secondEccentricitySquared)
                * Math.pow(normalizedEasting, 4) / 24.0
                + (61.0 + 90.0 * tangentSquared + 298.0 * etaSquared
                + 45.0 * tangentSquared * tangentSquared
                - 252.0 * secondEccentricitySquared - 3.0 * etaSquared * etaSquared)
                * Math.pow(normalizedEasting, 6) / 720.0);

        double longitude = centralMeridian
                + (normalizedEasting
                - (1.0 + 2.0 * tangentSquared + etaSquared) * Math.pow(normalizedEasting, 3) / 6.0
                + (5.0 - 2.0 * etaSquared + 28.0 * tangentSquared
                - 3.0 * etaSquared * etaSquared
                + 8.0 * secondEccentricitySquared
                + 24.0 * tangentSquared * tangentSquared)
                * Math.pow(normalizedEasting, 5) / 120.0) / cosFootprint;

        return new Wgs84Coordinate(Math.toDegrees(longitude), Math.toDegrees(latitude));
    }

    record GeometryCenter(Double centerX, Double centerY, Integer srid) {
    }

    private record Wgs84Coordinate(double longitude, double latitude) {
    }

    private record BoundingBox(double minX, double minY, double maxX, double maxY) {

        BoundingBox include(double x, double y) {
            return new BoundingBox(
                    Math.min(minX, x),
                    Math.min(minY, y),
                    Math.max(maxX, x),
                    Math.max(maxY, y)
            );
        }
    }
}
