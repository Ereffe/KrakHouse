import { filters, type FilterKey } from "./mapFilters";
import { CombinedFilterSection } from "./CombinedFilterSection";
import { SingleFilterSection } from "./SingleFilterSection";
import { SettingsSection } from "./SettingsSection";
import { AccessibilitySection } from "./AccessibilitySection";

interface MapSidebarProps {
    selectedFilter: FilterKey;
    setSelectedFilter: (value: FilterKey) => void;
    minValue: number;
    setMinValue: (value: number) => void;
    maxValue: number;
    setMaxValue: (value: number) => void;
    language: "pl" | "en";
    setLanguage: (value: "pl" | "en") => void;
    darkMode: boolean;
    setDarkMode: (value: boolean) => void;
    gridSize: number;
    setGridSize: (value: number) => void;
    combinedMode: boolean;
    setCombinedMode: (value: boolean) => void;
    selectedFilters: FilterKey[];
    minMaxPerFilter: Record<FilterKey, { min: number; max: number }>;
    highContrast: boolean;
    setHighContrast: (value: boolean) => void;
    visuallyImpaired: boolean;
    setVisuallyImpaired: (value: boolean) => void;
    colorblind: boolean;
    setColorblind: (value: boolean) => void;
    panelTitleColor: string;
    panelTextColor: string;
    panelMutedColor: string;
    baseFontSize: string;
    titleFontSize: string;
    subTitleFontSize: string;
    formattedMinValue: string;
    formattedMaxValue: string;
    toggleCombinedFilter: (filterKey: FilterKey, checked: boolean) => void;
    updateCombinedFilterRange: (
        filterKey: FilterKey,
        rangeType: "min" | "max",
        value: number,
    ) => void;
}

export function MapSidebar({
    selectedFilter,
    setSelectedFilter,
    minValue,
    setMinValue,
    maxValue,
    setMaxValue,
    language,
    setLanguage,
    darkMode,
    setDarkMode,
    gridSize,
    setGridSize,
    combinedMode,
    setCombinedMode,
    selectedFilters,
    minMaxPerFilter,
    highContrast,
    setHighContrast,
    visuallyImpaired,
    setVisuallyImpaired,
    colorblind,
    setColorblind,
    panelTitleColor,
    panelTextColor,
    panelMutedColor,
    baseFontSize,
    titleFontSize,
    subTitleFontSize,
    formattedMinValue,
    formattedMaxValue,
    toggleCombinedFilter,
    updateCombinedFilterRange,
}: Readonly<MapSidebarProps>) {
    const selectedFilterConfig =
        filters.find((filter) => filter.key === selectedFilter) ?? filters[0];
    const visuallyImpairedFontSize = visuallyImpaired ? "26px" : "16px";

    return (
        <div
            style={{
                width: "280px",
                padding: "25px",
                background: darkMode
                    ? "linear-gradient(135deg, #2c2f33 0%, #23272a 100%)"
                    : "linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%)",
                borderRight: "2px solid #dee2e6",
                overflowY: "auto",
                boxShadow: "2px 0 10px rgba(0, 0, 0, 0.1)",
                borderRadius: "0 15px 15px 0",
                color: darkMode ? "#f8f9fa" : "#343a40",
            }}
        >
            <h3
                style={{
                    marginTop: 0,
                    marginBottom: "25px",
                    fontSize: titleFontSize,
                    color: panelTitleColor,
                    fontWeight: "600",
                    textAlign: "center",
                    borderBottom: "2px solid #6c757d",
                    paddingBottom: "10px",
                }}
            >
                Filtry
            </h3>
            <div style={{ marginBottom: "20px" }}>
                <label
                    style={{
                        display: "flex",
                        alignItems: "center",
                        cursor: "pointer",
                        fontSize: baseFontSize,
                        color: panelTextColor,
                        fontWeight: "500",
                    }}
                >
                    <input
                        type="checkbox"
                        checked={combinedMode}
                        onChange={(e) => setCombinedMode(e.target.checked)}
                        style={{
                            marginRight: "10px",
                            width: "18px",
                            height: "18px",
                            accentColor: "#28a745",
                        }}
                    />
                    Tryb połączonych filtrów
                </label>
            </div>

            {combinedMode ? (
                <>
                    <CombinedFilterSection
                        selectedFilters={selectedFilters}
                        minMaxPerFilter={minMaxPerFilter}
                        panelTextColor={panelTextColor}
                        baseFontSize={baseFontSize}
                        toggleCombinedFilter={toggleCombinedFilter}
                        updateCombinedFilterRange={updateCombinedFilterRange}
                    />
                    {selectedFilters.length > 0 && (
                        <div style={{ marginBottom: "25px" }}>
                            <h4
                                style={{
                                    color: panelTitleColor,
                                    fontSize: "16px",
                                    marginBottom: "10px",
                                }}
                            >
                                Wybrane filtry:
                            </h4>
                            {selectedFilters.map((filterKey) => (
                                <div
                                    key={filterKey}
                                    style={{
                                        color: "#28a745",
                                        fontWeight: "bold",
                                        fontSize: "14px",
                                    }}
                                >
                                    {filters.find((filter) => filter.key === filterKey)?.label}
                                </div>
                            ))}
                        </div>
                    )}
                </>
            ) : (
                <SingleFilterSection
                    selectedFilter={selectedFilter}
                    selectedFilterConfig={selectedFilterConfig}
                    minValue={minValue}
                    maxValue={maxValue}
                    panelTextColor={panelTextColor}
                    baseFontSize={baseFontSize}
                    formattedMinValue={formattedMinValue}
                    formattedMaxValue={formattedMaxValue}
                    setSelectedFilter={setSelectedFilter}
                    setMinValue={setMinValue}
                    setMaxValue={setMaxValue}
                />
            )}

            <div
                style={{
                    marginTop: "30px",
                    paddingTop: "20px",
                    borderTop: darkMode ? "1px solid #495057" : "1px solid #ced4da",
                }}
            >
                <SettingsSection
                    language={language}
                    darkMode={darkMode}
                    gridSize={gridSize}
                    panelTextColor={panelTextColor}
                    panelMutedColor={panelMutedColor}
                    subTitleFontSize={subTitleFontSize}
                    setLanguage={setLanguage}
                    setDarkMode={setDarkMode}
                    setGridSize={setGridSize}
                />
                <AccessibilitySection
                    highContrast={highContrast}
                    visuallyImpaired={visuallyImpaired}
                    colorblind={colorblind}
                    panelTextColor={panelTextColor}
                    visuallyImpairedFontSize={visuallyImpairedFontSize}
                    setHighContrast={setHighContrast}
                    setVisuallyImpaired={setVisuallyImpaired}
                    setColorblind={setColorblind}
                />
            </div>
        </div>
    );
}
