package pk.backend.domain.CityMap;

import pk.backend.domain.box.BoxValue;
import pk.backend.domain.utils.CompareCondition;

import java.util.List;

public interface CityMap {
    void applyFilter(BoxValue value, CompareCondition condition);
    CityMap merge(CityMap map);
    CityMap mergeAll(List<CityMap> map);
}
