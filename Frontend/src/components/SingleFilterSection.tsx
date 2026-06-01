import { RangeSlider } from "./RangeSlider";
import type { FilterDefinition, FilterKey } from "./mapFilters";
import { useTheme } from "./ThemeContext";

interface SingleFilterSectionProps {
    readonly filters: FilterDefinition[];
    readonly selectedFilter: FilterKey;
    readonly selectedFilterConfig?: { min: number; max: number };
    readonly minValue: number;
    readonly maxValue: number;
    readonly formattedMinValue: string;
    readonly formattedMaxValue: string;
    readonly setSelectedFilter: (value: FilterKey) => void;
    readonly setMinValue: (value: number) => void;
    readonly setMaxValue: (value: number) => void;
}

function getSliderGradient(filterKey: FilterKey) {
    return filterKey === "PRICE"
        ? "linear-gradient(to right, #0d9488, #f97316, #ef4444)"
        : "linear-gradient(to right, #ef4444, #f97316, #0d9488)";
}

export function SingleFilterSection({
    filters,
    selectedFilter,
    selectedFilterConfig,
    minValue,
    maxValue,
    formattedMinValue,
    formattedMaxValue,
    setSelectedFilter,
    setMinValue,
    setMaxValue,
}: SingleFilterSectionProps) {
    const { panelTextColor } = useTheme();

    if (!selectedFilterConfig) {
        return null;
    }

    return (
        <>
            <div style={{ marginBottom: "25px" }}>
                <div
                    style={{
                        display: "block",
                        marginBottom: "18px",
                        fontWeight: "600",
                        color: panelTextColor,
                        fontSize: "15px",
                        textTransform: "uppercase",
                        letterSpacing: "0.5px",
                        opacity: 0.8,
                    }}
                >
                    Wybierz filtr
                </div>
                {filters.map((filter) => (
                    <div key={filter.key} style={{ marginBottom: "12px" }}>
                        <label
                            style={{
                                display: "flex",
                                alignItems: "center",
                                cursor: "pointer",
                                fontSize: "14px",
                                color: selectedFilter === filter.key ? "#0d9488" : panelTextColor,
                                fontWeight: selectedFilter === filter.key ? "600" : "500",
                                padding: "8px 12px",
                                borderRadius: "8px",
                                transition: "all 0.2s ease",
                                backgroundColor: selectedFilter === filter.key ? "rgba(13, 148, 136, 0.1)" : "transparent",
                            }}
                        >
                            <input
                                type="radio"
                                name="filter"
                                value={filter.key}
                                checked={selectedFilter === filter.key}
                                onChange={(e) => setSelectedFilter(e.target.value as FilterKey)}
                                style={{
                                    marginRight: "10px",
                                    accentColor: "#0d9488",
                                    cursor: "pointer",
                                }}
                            />
                            {filter.label}
                        </label>
                    </div>
                ))}
            </div>
            <RangeSlider
                label="Minimum"
                value={minValue}
                displayValue={formattedMinValue}
                min={selectedFilterConfig.min}
                max={selectedFilterConfig.max}
                step={selectedFilter === "PRICE" ? 10 : 1}
                onChange={setMinValue}
                labelColor={panelTextColor}
                valueColor="#ef4444"
                trackGradient={getSliderGradient(selectedFilter)}
            />
            <RangeSlider
                label="Maximum"
                value={maxValue}
                displayValue={formattedMaxValue}
                min={selectedFilterConfig.min}
                max={selectedFilterConfig.max}
                step={selectedFilter === "PRICE" ? 10 : 1}
                onChange={setMaxValue}
                labelColor={panelTextColor}
                valueColor="#0d9488"
                trackGradient={getSliderGradient(selectedFilter)}
                containerStyle={{ marginTop: "18px" }}
            />
        </>
    );
}
