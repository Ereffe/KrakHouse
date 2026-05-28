package pk.backend.domain.model.CityMap;

import pk.backend.domain.model.box.ValueObjects.BoxValue;

import java.util.List;

public class GridMap implements CityMap {

    public static final double LATITUDE_LEFT_BORDER = 50.118461;
    public static final double LATITUDE_RIGHT_BORDER = 50.124014;

    public static final double LONGITUDE_TOP_BORDER = 19.813891;
    public static final double LONGITUDE_BOTTOM_BORDER = 19.856463;

    private List<List<BoxValue>> boxMatrix;

    private String dataProvider;

    public GridMap(List<List<BoxValue>> boxMatrix, String dataProvider) {
        this.boxMatrix = boxMatrix;
        this.dataProvider = dataProvider;
    }

    public GridMap(List<List<BoxValue>> boxMatrix) {
        this.boxMatrix = boxMatrix;
        this.dataProvider = "";
    }

    @Override
    public void applyFilter(BoxValue min, BoxValue max) {
        for (List<BoxValue> row : boxMatrix) {
            for (int j = 0; j < row.size(); j++) {
                BoxValue current = row.get(j);
                if (current == null) continue;

                if (current.compareTo(min) < 0 || current.compareTo(max) > 0) {
                    row.set(j, null);
                }
            }
        }
    }

    @Override
    public CityMap merge(CityMap map) {
        List<List<BoxValue>> otherMatrix = map.getBoxMatrix();
        for (int i = 0; i < boxMatrix.size(); i++) {
            List<BoxValue> row = boxMatrix.get(i);
            List<BoxValue> otherRow = otherMatrix.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j) == null || otherRow.get(j) == null) {
                    row.set(j, null);
                }
            }
        }
        return this;
    }

    @Override
    public CityMap mergeAll(List<CityMap> maps) {
        for (CityMap map : maps) {
            merge(map);
        }
        return this;
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

    @Override
    public String getDataProvider() {
        return dataProvider;
    }
}
