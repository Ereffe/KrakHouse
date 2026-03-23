package pk.backend.domain.CityMap;

import pk.backend.domain.box.BoxValue;
import pk.backend.domain.utils.CompareCondition;

public interface CityMap {
    void applyFilter(BoxValue value, CompareCondition condition);
    CityMap merge(CityMap map);
}
