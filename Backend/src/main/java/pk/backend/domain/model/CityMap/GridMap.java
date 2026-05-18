package pk.backend.domain.model.CityMap;

import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.domain.model.utils.CompareCondition;

import java.util.List;

public class GridMap implements CityMap{

    private List<List<BoxValue>> boxMatrix;

    public GridMap(List<List<BoxValue>> boxMatrix) {
        this.boxMatrix = boxMatrix;
    }

    @Override
    public void applyFilter(BoxValue value, CompareCondition condition) {
//        TODO: 3 implement map methods
        throw new UnsupportedOperationException("Not implemented yet");
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
}