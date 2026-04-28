package pk.backend.domain.model.CityMap;

import pk.backend.domain.model.box.BoxValue;
import pk.backend.domain.model.utils.CompareCondition;

import java.util.List;

public interface CityMap {
    void applyFilter(BoxValue value, CompareCondition condition);
    CityMap merge(CityMap map);
    CityMap mergeAll(List<CityMap> map);
}
