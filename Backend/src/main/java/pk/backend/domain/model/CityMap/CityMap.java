package pk.backend.domain.model.CityMap;

import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.domain.model.utils.CompareCondition;

import java.util.List;

public interface CityMap {
    void applyFilter(BoxValue value, CompareCondition condition);
    void applyFilter(BoxValue min, BoxValue max);
    CityMap merge(CityMap map);
    CityMap mergeAll(List<CityMap> map);

    double getLatitudeLeftBorder();
    double getLatitudeRightBorder();
    double getLongitudeTopBorder();
    double getLongitudeBottomBorder();
    
    List<List<BoxValue>> getBoxMatrix();
}
