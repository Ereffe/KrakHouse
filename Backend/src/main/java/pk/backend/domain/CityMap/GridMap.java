package pk.backend.domain.CityMap;

import pk.backend.domain.box.BoxValue;
import pk.backend.domain.utils.CompareCondition;

import java.util.List;

public class GridMap implements CityMap{

    private List<List<BoxValue>> boxMatrix;

    public GridMap(List<List<BoxValue>> boxMatrix) {
        this.boxMatrix = boxMatrix;
    }

    @Override
    public void applyFilter(BoxValue value, CompareCondition condition) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CityMap merge(CityMap map) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}