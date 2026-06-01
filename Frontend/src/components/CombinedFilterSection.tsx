import { RangeSlider } from "./RangeSlider";
import type { FilterDefinition, FilterKey } from "./mapFilters";
import { useTheme } from "./ThemeContext";

interface CombinedFilterSectionProps {
    readonly filters: FilterDefinition[];
    readonly selectedFilters: FilterKey[];
    readonly minMaxPerFilter: Record<FilterKey, { min: number; max: number }>;
    readonly toggleCombinedFilter: (filterKey: FilterKey, checked: boolean) => void;
    readonly updateCombinedFilterRange: (
        filterKey: FilterKey,
        rangeType: "min" | "max",
        value: number,
    ) => void;
}

function getSliderStep(filterKey: FilterKey) {
    return filterKey === "PRICE" ? 10 : 1;
}

export function CombinedFilterSection({
    filters,
    selectedFilters,
    minMaxPerFilter,
    toggleCombinedFilter,
    updateCombinedFilterRange,
}: CombinedFilterSectionProps) {
    const { panelTextColor } = useTheme();

    return (
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
                Wybierz filtry
            </div>

            {filters.map((filter) => {
                const selectedRange = minMaxPerFilter[filter.key] ?? {
                    min: filter.min,
                    max: filter.max,
                };

                return (
                    <div key={filter.key} style={{ marginBottom: "12px" }}>
                        <label
                            style={{
                                display: "flex",
                                alignItems: "center",
                                cursor: "pointer",
                                fontSize: "14px",
                                color: selectedFilters.includes(filter.key) ? "#0d9488" : panelTextColor,
                                fontWeight: selectedFilters.includes(filter.key) ? "600" : "500",
                                padding: "8px 12px",
                                borderRadius: "8px",
                                transition: "all 0.2s ease",
                                backgroundColor: selectedFilters.includes(filter.key)
                                    ? "rgba(13, 148, 136, 0.1)"
                                    : "transparent",
                            }}
                        >
                            <input
                                type="checkbox"
                                checked={selectedFilters.includes(filter.key)}
                                onChange={(e) => toggleCombinedFilter(filter.key, e.target.checked)}
                                style={{
                                    marginRight: "10px",
                                    accentColor: "#0d9488",
                                    cursor: "pointer",
                                }}
                            />
                            {filter.label}
                        </label>

                        {selectedFilters.includes(filter.key) && (
                            <div
                                style={{
                                    marginLeft: "30px",
                                    marginTop: "12px",
                                    paddingLeft: "12px",
                                    borderLeft: "2px solid rgba(13, 148, 136, 0.3)",
                                }}
                            >
                                <RangeSlider
                                    label="Min"
                                    value={selectedRange.min}
                                    displayValue={String(selectedRange.min)}
                                    min={filter.min}
                                    max={selectedRange.max}
                                    step={getSliderStep(filter.key)}
                                    onChange={(value) => updateCombinedFilterRange(filter.key, "min", value)}
                                    labelColor={panelTextColor}
                                    valueColor="#0d9488"
                                    trackGradient="linear-gradient(to right, #ef4444, #0d9488)"
                                    containerStyle={{ marginBottom: "14px" }}
                                    labelRowStyle={{ fontSize: "12px" }}
                                />
                                <RangeSlider
                                    label="Max"
                                    value={selectedRange.max}
                                    displayValue={String(selectedRange.max)}
                                    min={selectedRange.min}
                                    max={filter.max}
                                    step={getSliderStep(filter.key)}
                                    onChange={(value) => updateCombinedFilterRange(filter.key, "max", value)}
                                    labelColor={panelTextColor}
                                    valueColor="#0d9488"
                                    trackGradient="linear-gradient(to right, #ef4444, #0d9488)"
                                    containerStyle={{ marginBottom: "0" }}
                                    labelRowStyle={{ fontSize: "12px", marginTop: "12px" }}
                                />
                            </div>
                        )}
                    </div>
                );
            })}
        </div>
    );
}
