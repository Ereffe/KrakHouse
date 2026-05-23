package pk.backend.infrastructure.utility;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class AQIParameters {

    /**
     * Klasa narzędziowa dostarczająca parametry AQI (Air Quality Index) zgodnie ze standardem amerykańskim.
     * Zawiera wartości graniczne dla 6 głównych zanieczyszczeń oraz przeliczniki jednostek z µg/m³ na ppm/ppb.
     */

    // Breakpoints dla PM2.5 (µg/m³) - 24-godzinna średnia
    private static final double[][] PM25_BREAKPOINTS = {
        {0, 12.0, 0, 50},
        {12.1, 35.4, 51, 100},
        {35.5, 55.4, 101, 150},
        {55.5, 150.4, 151, 200},
        {150.5, 250.4, 201, 300},
        {250.5, 500.4, 301, 500}
    };

    // Breakpoints dla PM10 (µg/m³) - 24-godzinna średnia
    private static final double[][] PM10_BREAKPOINTS = {
        {0, 54.0, 0, 50},
        {54.1, 154.0, 51, 100},
        {154.1, 254.0, 101, 150},
        {254.1, 354.0, 151, 200},
        {354.1, 424.0, 201, 300},
        {424.1, 604.0, 301, 500}
    };

    // Breakpoints dla O3 (ppm) - 8-godzinna średnia
    private static final double[][] O3_BREAKPOINTS = {
        {0, 0.054, 0, 50},
        {0.055, 0.070, 51, 100},
        {0.071, 0.085, 101, 150},
        {0.086, 0.105, 151, 200},
        {0.106, 0.200, 201, 300},
        {0.201, 0.604, 301, 500}
    };

    // Breakpoints dla NO2 (ppb) - 1-godzinna średnia
    private static final double[][] NO2_BREAKPOINTS = {
        {0, 53.0, 0, 50},
        {54.0, 100.0, 51, 100},
        {101.0, 360.0, 101, 150},
        {361.0, 649.0, 151, 200},
        {650.0, 1248.0, 201, 300},
        {1249.0, 2049.0, 301, 500}
    };

    // Breakpoints dla SO2 (ppb) - 1-godzinna średnia
    private static final double[][] SO2_BREAKPOINTS = {
        {0, 35.0, 0, 50},
        {36.0, 75.0, 51, 100},
        {76.0, 185.0, 101, 150},
        {186.0, 304.0, 151, 200},
        {305.0, 604.0, 201, 300},
        {605.0, 1004.0, 301, 500}
    };

    // Breakpoints dla CO (ppm) - 8-godzinna średnia
    private static final double[][] CO_BREAKPOINTS = {
        {0, 4.4, 0, 50},
        {4.5, 9.4, 51, 100},
        {9.5, 12.4, 101, 150},
        {12.5, 15.4, 151, 200},
        {15.5, 30.4, 201, 300},
        {30.5, 50.4, 301, 500}
    };

    private static final Map<String, double[][]> POLLUTANT_BREAKPOINTS = new HashMap<>();

    static {
        POLLUTANT_BREAKPOINTS.put("PM2.5", PM25_BREAKPOINTS);
        POLLUTANT_BREAKPOINTS.put("PM10", PM10_BREAKPOINTS);
        POLLUTANT_BREAKPOINTS.put("O3", O3_BREAKPOINTS);
        POLLUTANT_BREAKPOINTS.put("NO2", NO2_BREAKPOINTS);
        POLLUTANT_BREAKPOINTS.put("SO2", SO2_BREAKPOINTS);
        POLLUTANT_BREAKPOINTS.put("CO", CO_BREAKPOINTS);
    }

    /**
     * Przetwarza mapę odczytów z sensorów na parametry potrzebne do obliczenia AQI.
     * @param sensors mapa (nazwa zanieczyszczenia -> wartość w µg/m³)
     * @return mapa (nazwa -> tablica [bp_lo, bp_hi, i_lo, i_hi, converted_value])
     */
    public static Map<String, double[]> getParamsForSensors(Map<String, Double> sensors) {
        Map<String, double[]> result = new HashMap<>();
        for (Map.Entry<String, Double> entry : sensors.entrySet()) {
            String pollutant = entry.getKey();
            Double value = entry.getValue();
            if (value == null) continue;

            double convertedValue = convertToUSUnits(pollutant, value);
            double[] params = getAQIParameters(pollutant, convertedValue);

            if (params != null) {
                double[] fullParams = new double[5];
                System.arraycopy(params, 0, fullParams, 0, 4);
                fullParams[4] = convertedValue;
                result.put(pollutant, fullParams);
            }
        }
        return result;
    }

    private static double convertToUSUnits(String pollutant, double value) {
        // Konwersja z µg/m³ na jednostki EPA (ppm/ppb) przy 25°C
        return switch (pollutant) {
            case "O3" -> value * 0.000509; // µg/m³ -> ppm
            case "NO2" -> value * 0.5315;  // µg/m³ -> ppb
            case "SO2" -> value * 0.3814;  // µg/m³ -> ppb
            case "CO" -> value * 0.000873; // µg/m³ -> ppm
            default -> value;              // PM2.5, PM10 zostają w µg/m³
        };
    }

    private static double[] getAQIParameters(String pollutantName, Double value) {
        if (value == null || value < 0) {
            return null;
        }

        double[][] breakpoints = POLLUTANT_BREAKPOINTS.get(pollutantName);
        if (breakpoints == null) {
            return null;
        }

        for (double[] breakpoint : breakpoints) {
            if (value >= breakpoint[0] && value <= breakpoint[1]) {
                return breakpoint;
            }
        }

        // Jeśli wartość przekracza maksymalny breakpoint, zwróć ostatni
        return breakpoints[breakpoints.length - 1];
    }
}



