import { RangeSlider } from "./RangeSlider";
import { filters, type FilterKey } from "./mapFilters";
import { useTheme } from "./ThemeContext";

interface CombinedFilterSectionProps {
    readonly selectedFilters: FilterKey[];
    readonly minMaxPerFilter: Record<FilterKey, { min: number; max: number }>;
    readonly toggleCombinedFilter: (filterKey: FilterKey, checked: boolean) => void;
    readonly updateCombinedFilterRange: (
        filterKey: FilterKey,
        rangeType: "min" | "max",
        value: number,
    ) => void;
}

export function CombinedFilterSection({
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

            {filters.map((filter) => (
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
                                value={minMaxPerFilter[filter.key].min}
                                displayValue={String(minMaxPerFilter[filter.key].min)}
                                min={filter.min}
                                max={filter.max}
                                step={filter.key === "lifeScore" ? 1 : 0.1}
                                onChange={(value) => updateCombinedFilterRange(filter.key, "min", value)}
                                labelColor={panelTextColor}
                                valueColor="#0d9488"
                                trackGradient="linear-gradient(to right, #ef4444, #0d9488)"
                                containerStyle={{ marginBottom: "14px" }}
                                labelRowStyle={{ fontSize: "12px" }}
                            />
                            <RangeSlider
                                label="Max"
                                value={minMaxPerFilter[filter.key].max}
                                displayValue={String(minMaxPerFilter[filter.key].max)}
                                min={filter.min}
                                max={filter.max}
                                step={filter.key === "lifeScore" ? 1 : 0.1}
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
            ))}
        </div>
    );
}
