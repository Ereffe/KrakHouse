package pk.backend.domain.model.box;

import org.junit.jupiter.api.Test;
import pk.backend.domain.model.Exceptions.BoxObjectMismatchException;
import pk.backend.domain.model.Exceptions.BoxValueOutOfRangeException;
import pk.backend.domain.model.Exceptions.InvalidBoxValueException;
import pk.backend.domain.model.box.ValueObjects.PriceBoxObject;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BoxObjectTest {
    @Test
    void RoundPriceToTwoDecimals() {
        PriceBox box = new PriceBox(new BigDecimal("123.456"));
        assertEquals(new BigDecimal("123.46"), box.getValue().rawValue());
    }

    @Test
    void RejectNegativePrice() {
        assertThrows(InvalidBoxValueException.class,
                () -> new PriceBoxObject(new BigDecimal("-1.00")));
    }

    @Test
    void RejectNoiseOutsideRange() {
        assertThrows(BoxValueOutOfRangeException.class,
                () -> new NoiseBox(250));
    }

    @Test
    void CompareSameTypeBoxes() {
        assertTrue(new NoiseBox(40).compareTo(new NoiseBox(50)) < 0);
    }

    @Test
    void RejectComparingDifferent() {
        assertThrows(BoxObjectMismatchException.class,
                () -> new NoiseBox(40).compareTo(new CrimeBox(1)));
    }
}
