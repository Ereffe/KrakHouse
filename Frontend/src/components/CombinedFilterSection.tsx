import { filters, type FilterKey } from "./mapFilters";

interface CombinedFilterSectionProps {
    readonly selectedFilters: FilterKey[];
    readonly minMaxPerFilter: Record<FilterKey, { min: number; max: number }>;
    readonly panelTextColor: string;
    readonly baseFontSize: string;
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
    panelTextColor,
    baseFontSize,
    toggleCombinedFilter,
    updateCombinedFilterRange,
}: CombinedFilterSectionProps) {
    return (
        <div style={{ marginBottom: "25px" }}>
            <label
                style={{
                    display: "block",
                    marginBottom: "15px",
                    fontWeight: "500",
                    color: panelTextColor,
                    fontSize: baseFontSize,
                }}
            >
                Wybierz filtry:
            </label>
            {filters.map((filter) => (
                <div key={filter.key} style={{ marginBottom: "10px" }}>
                    <label
                        style={{
                            display: "flex",
                            alignItems: "center",
                            cursor: "pointer",
                            fontSize: "14px",
                            color: selectedFilters.includes(filter.key) ? "#28a745" : panelTextColor,
                            fontWeight: selectedFilters.includes(filter.key) ? "700" : "500",
                        }}
                    >
                        <input
                            type="checkbox"
                            checked={selectedFilters.includes(filter.key)}
                            onChange={(e) => toggleCombinedFilter(filter.key, e.target.checked)}
                            style={{
                                marginRight: "10px",
                            }}
                        />
                        {filter.label}
                    </label>
                    {selectedFilters.includes(filter.key) && (
                        <div style={{ marginLeft: "25px", marginTop: "10px" }}>
                            <div style={{ marginBottom: "10px" }}>
                                <label
                                    style={{
                                        display: "block",
                                        marginBottom: "5px",
                                        fontSize: "12px",
                                        color: panelTextColor,
                                    }}
                                >
                                    Min: {minMaxPerFilter[filter.key].min}
                                </label>
                                <input
                                    type="range"
                                    min={filter.min}
                                    max={filter.max}
                                    step={filter.key === "lifeScore" ? 1 : 0.1}
                                    value={minMaxPerFilter[filter.key].min}
                                    onChange={(e) =>
                                        updateCombinedFilterRange(filter.key, "min", Number(e.target.value))
                                    }
                                    style={{
                                        width: "100%",
                                        height: "6px",
                                        borderRadius: "5px",
                                        background: "linear-gradient(to right, #dc3545, #28a745)",
                                        outline: "none",
                                        cursor: "pointer",
                                    }}
                                />
                            </div>
                            <div>
                                <label
                                    style={{
                                        display: "block",
                                        marginBottom: "5px",
                                        fontSize: "12px",
                                        color: panelTextColor,
                                    }}
                                >
                                    Max: {minMaxPerFilter[filter.key].max}
                                </label>
                                <input
                                    type="range"
                                    min={filter.min}
                                    max={filter.max}
                                    step={filter.key === "lifeScore" ? 1 : 0.1}
                                    value={minMaxPerFilter[filter.key].max}
                                    onChange={(e) =>
                                        updateCombinedFilterRange(filter.key, "max", Number(e.target.value))
                                    }
                                    style={{
                                        width: "100%",
                                        height: "6px",
                                        borderRadius: "5px",
                                        background: "linear-gradient(to right, #dc3545, #28a745)",
                                        outline: "none",
                                        cursor: "pointer",
                                    }}
                                />
                            </div>
                        </div>
                    )}
                </div>
            ))}
        </div>
    );
}
