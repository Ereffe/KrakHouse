package pk.backend.domain.model.CityMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pk.backend.domain.model.box.AirQualityBox;
import pk.backend.domain.model.box.ValueObjects.BoxValue;
import pk.backend.domain.model.utils.CompareCondition;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class GridMapTest {

    @Test
    @DisplayName("Should filter out values that do not match the GREATER condition")
    void shouldFilterGreater() {
        List<List<BoxValue>> matrix = new ArrayList<>();
        List<BoxValue> row = new ArrayList<>();
        row.add(new AirQualityBox(10));
        row.add(new AirQualityBox(60));
        matrix.add(row);

        GridMap map = new GridMap(matrix);
        BoxValue threshold = new AirQualityBox(50);

        map.applyFilter(threshold, CompareCondition.GREATER);

        assertNull(row.get(0), "Value 10 should be filtered out (not > 50)");
        assertNotNull(row.get(1), "Value 60 should remain (is > 50)");
    }

    @Test
    @DisplayName("Should filter out values that do not match the LESS condition")
    void shouldFilterLess() {
        List<List<BoxValue>> matrix = new ArrayList<>();
        List<BoxValue> row = new ArrayList<>();
        row.add(new AirQualityBox(10));
        row.add(new AirQualityBox(60));
        matrix.add(row);

        GridMap map = new GridMap(matrix);
        BoxValue threshold = new AirQualityBox(50);

        map.applyFilter(threshold, CompareCondition.LESS);

        assertNotNull(row.get(0), "Value 10 should remain (is < 50)");
        assertNull(row.get(1), "Value 60 should be filtered out (not < 50)");
    }
}
