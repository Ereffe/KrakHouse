import { filters, type FilterKey } from "./mapFilters";

interface SingleFilterSectionProps {
    readonly selectedFilter: FilterKey;
    readonly selectedFilterConfig: { min: number; max: number };
    readonly minValue: number;
    readonly maxValue: number;
    readonly panelTextColor: string;
    readonly baseFontSize: string;
    readonly formattedMinValue: string;
    readonly formattedMaxValue: string;
    readonly setSelectedFilter: (value: FilterKey) => void;
    readonly setMinValue: (value: number) => void;
    readonly setMaxValue: (value: number) => void;
}

export function SingleFilterSection({
    selectedFilter,
    selectedFilterConfig,
    minValue,
    maxValue,
    panelTextColor,
    baseFontSize,
    formattedMinValue,
    formattedMaxValue,
    setSelectedFilter,
    setMinValue,
    setMaxValue,
}: SingleFilterSectionProps) {
    return (
        <>
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
                    Wybierz filtr:
                </label>
                {filters.map((filter) => (
                    <div key={filter.key} style={{ marginBottom: "10px" }}>
                        <label
                            style={{
                                display: "flex",
                                alignItems: "center",
                                cursor: "pointer",
                                fontSize: "14px",
                                color: selectedFilter === filter.key ? "#28a745" : panelTextColor,
                                fontWeight: selectedFilter === filter.key ? "700" : "500",
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
                                }}
                            />
                            {filter.label}
                        </label>
                    </div>
                ))}
            </div>
            <div>
                <label
                    style={{
                        display: "block",
                        marginBottom: "8px",
                        fontWeight: "500",
                        color: panelTextColor,
                        fontSize: baseFontSize,
                    }}
                >
                    Min:{" "}
                    <span style={{ color: "#dc3545", fontWeight: "bold" }}>{formattedMinValue}</span>
                </label>
                <input
                    type="range"
                    min={selectedFilterConfig.min}
                    max={selectedFilterConfig.max}
                    step={selectedFilter === "lifeScore" ? 1 : 0.1}
                    value={minValue}
                    onChange={(e) => setMinValue(Number(e.target.value))}
                    style={{
                        width: "100%",
                        height: "8px",
                        borderRadius: "10px",
                        background: "linear-gradient(to right, #dc3545, #ffc107, #28a745)",
                        outline: "none",
                        cursor: "pointer",
                        appearance: "none",
                        WebkitAppearance: "none",
                    }}
                />
            </div>
            <div style={{ marginTop: "15px" }}>
                <label
                    style={{
                        display: "block",
                        marginBottom: "8px",
                        fontWeight: "500",
                        color: panelTextColor,
                        fontSize: baseFontSize,
                    }}
                >
                    Max:{" "}
                    <span style={{ color: "#28a745", fontWeight: "bold" }}>{formattedMaxValue}</span>
                </label>
                <input
                    type="range"
                    min={selectedFilterConfig.min}
                    max={selectedFilterConfig.max}
                    step={selectedFilter === "lifeScore" ? 1 : 0.1}
                    value={maxValue}
                    onChange={(e) => setMaxValue(Number(e.target.value))}
                    style={{
                        width: "100%",
                        height: "8px",
                        borderRadius: "10px",
                        background: "linear-gradient(to right, #dc3545, #ffc107, #28a745)",
                        outline: "none",
                        cursor: "pointer",
                        appearance: "none",
                        WebkitAppearance: "none",
                    }}
                />
            </div>
        </>
    );
}
