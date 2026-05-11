import { RangeSlider } from "./RangeSlider";
import { filters, type FilterKey } from "./mapFilters";
import { useTheme } from "./ThemeContext";

interface SingleFilterSectionProps {
    readonly selectedFilter: FilterKey;
    readonly selectedFilterConfig: { min: number; max: number };
    readonly formattedMinValue: string;
    readonly formattedMaxValue: string;
    readonly setSelectedFilter: (value: FilterKey) => void;
    readonly setMinValue: (value: number) => void;
    readonly setMaxValue: (value: number) => void;
}

export function SingleFilterSection({
    selectedFilter,
    selectedFilterConfig,
    formattedMinValue,
    formattedMaxValue,
    setSelectedFilter,
    setMinValue,
    setMaxValue,
}: SingleFilterSectionProps) {
    const { panelTextColor } = useTheme();

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
                value={selectedFilter === "lifeScore" ? Number(formattedMinValue.replace("%", "")) : Number(formattedMinValue.replace(" PLN", ""))}
                displayValue={formattedMinValue}
                min={selectedFilterConfig.min}
                max={selectedFilterConfig.max}
                step={selectedFilter === "lifeScore" ? 1 : 0.1}
                onChange={setMinValue}
                labelColor={panelTextColor}
                valueColor="#ef4444"
                trackGradient="linear-gradient(to right, #ef4444, #f97316, #0d9488)"
            />
            <RangeSlider
                label="Maximum"
                value={selectedFilter === "lifeScore" ? Number(formattedMaxValue.replace("%", "")) : Number(formattedMaxValue.replace(" PLN", ""))}
                displayValue={formattedMaxValue}
                min={selectedFilterConfig.min}
                max={selectedFilterConfig.max}
                step={selectedFilter === "lifeScore" ? 1 : 0.1}
                onChange={setMaxValue}
                labelColor={panelTextColor}
                valueColor="#0d9488"
                trackGradient="linear-gradient(to right, #ef4444, #f97316, #0d9488)"
                containerStyle={{ marginTop: "18px" }}
            />
        </>
    );
}
