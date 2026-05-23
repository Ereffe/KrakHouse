package pk.backend.domain.model.CityMap;

import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.domain.model.utils.CompareCondition;

import java.util.List;

public class GridMap implements CityMap {

    public static final double LATITUDE_LEFT_BORDER = 50.118461;
    public static final double LATITUDE_RIGHT_BORDER = 50.124014;

    public static final double LONGITUDE_TOP_BORDER = 19.813891;
    public static final double LONGITUDE_BOTTOM_BORDER = 19.856463;

    private List<List<BoxValue>> boxMatrix;

    public GridMap(List<List<BoxValue>> boxMatrix) {
        this.boxMatrix = boxMatrix;
    }

    @Override
    public void applyFilter(BoxValue value, CompareCondition condition) {
        for (List<BoxValue> row : boxMatrix) {
            for (int j = 0; j < row.size(); j++) {
                BoxValue current = row.get(j);
                if (current == null) continue;

                boolean matches = switch (condition) {
                    case LESS -> current.compareTo(value) < 0;
                    case LESS_EQUAL -> current.compareTo(value) <= 0;
                    case EQUAL -> current.compareTo(value) == 0;
                    case GREATER_EQUAL -> current.compareTo(value) >= 0;
                    case GREATER -> current.compareTo(value) > 0;
                };

                if (!matches) {
                    row.set(j, null);
                }
            }
        }
    }

    @Override
    public CityMap merge(CityMap map) {
        //        TODO: 3 implement map methods
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CityMap mergeAll(List<CityMap> map) {
        //        TODO: 3 implement map methods
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public double getLatitudeLeftBorder() {
        return LATITUDE_LEFT_BORDER;
    }

    @Override
    public double getLatitudeRightBorder() {
        return LATITUDE_RIGHT_BORDER;
    }

    @Override
    public double getLongitudeTopBorder() {
        return LONGITUDE_TOP_BORDER;
    }

    @Override
    public double getLongitudeBottomBorder() {
        return LONGITUDE_BOTTOM_BORDER;
    }

    @Override
    public List<List<BoxValue>> getBoxMatrix() {
        return boxMatrix;
    }
}
