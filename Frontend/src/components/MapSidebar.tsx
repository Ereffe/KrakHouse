import { useState } from "react";
import { filters, type FilterKey } from "./mapFilters";
import { CombinedFilterSection } from "./CombinedFilterSection";
import { SingleFilterSection } from "./SingleFilterSection";
import { SettingsSection } from "./SettingsSection";
import { AccessibilitySection } from "./AccessibilitySection";
import { useTheme } from "./ThemeContext";

interface MapSidebarProps {
    selectedFilter: FilterKey;
    setSelectedFilter: (value: FilterKey) => void;
    setMinValue: (value: number) => void;
    setMaxValue: (value: number) => void;
    language: "pl" | "en";
    setLanguage: (value: "pl" | "en") => void;
    setDarkMode: (value: boolean) => void;
    gridSize: number;
    setGridSize: (value: number) => void;
    combinedMode: boolean;
    setCombinedMode: (value: boolean) => void;
    selectedFilters: FilterKey[];
    minMaxPerFilter: Record<FilterKey, { min: number; max: number }>;
    highContrast: boolean;
    setHighContrast: (value: boolean) => void;
    setVisuallyImpaired: (value: boolean) => void;
    colorblind: boolean;
    setColorblind: (value: boolean) => void;
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
    setMinValue,
    setMaxValue,
    language,
    setLanguage,
    setDarkMode,
    gridSize,
    setGridSize,
    combinedMode,
    setCombinedMode,
    selectedFilters,
    minMaxPerFilter,
    highContrast,
    setHighContrast,
    setVisuallyImpaired,
    colorblind,
    setColorblind,
    formattedMinValue,
    formattedMaxValue,
    toggleCombinedFilter,
    updateCombinedFilterRange,
}: Readonly<MapSidebarProps>) {
    const [isCollapsed, setIsCollapsed] = useState(false);
    const {
        darkMode,
        panelTitleColor,
        panelTextColor,
        panelBorderColor,
        sidebarBackground,
        titleFontSize,
        baseFontSize,
    } = useTheme();
    const selectedFilterConfig =
        filters.find((filter) => filter.key === selectedFilter) ?? filters[0];

    return (
        <div
            style={{
                width: isCollapsed ? "52px" : "280px",
                padding: isCollapsed ? "12px 4px" : "25px",
                background: sidebarBackground,
                overflowY: "auto",
                boxShadow: "2px 0 10px rgba(0, 0, 0, 0.1)",
                color: darkMode ? "#f8f9fa" : "#343a40",
                transition: "width 0.3s ease, padding 0.3s ease",
                display: "flex",
                flexDirection: "column",
            }}
        >
            <button
                onClick={() => setIsCollapsed(!isCollapsed)}
                style={{
                    width: "42px",
                    height: "44px",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    background: "rgba(13, 148, 136, 0.15)",
                    border: "1.5px solid #0d9488",
                    borderRadius: "8px",
                    color: "#0d9488",
                    cursor: "pointer",
                    fontSize: "20px",
                    fontWeight: "600",
                    transition: "all 0.2s ease",
                    marginBottom: "15px",
                    marginLeft: isCollapsed ? "0.35rem" : "0",
                    flexShrink: 0,
                }}
                title={isCollapsed ? "Expand sidebar" : "Collapse sidebar"}
            >
                {isCollapsed ? "→" : "←"}
            </button>

            {!isCollapsed && (
                <>
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
                                    accentColor: "#0d9488",
                                    cursor: "pointer",
                                }}
                            />{" "}
                            Tryb połączonych filtrów
                        </label>
                    </div>

                    {combinedMode ? (
                        <>
                            <CombinedFilterSection
                                selectedFilters={selectedFilters}
                                minMaxPerFilter={minMaxPerFilter}
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
                                                color: "#0d9488",
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
                            borderTop: `1px solid ${panelBorderColor}`,
                        }}
                    >
                        <SettingsSection
                            language={language}
                            gridSize={gridSize}
                            setLanguage={setLanguage}
                            setDarkMode={setDarkMode}
                            setGridSize={setGridSize}
                        />
                        <AccessibilitySection
                            highContrast={highContrast}
                            colorblind={colorblind}
                            setHighContrast={setHighContrast}
                            setVisuallyImpaired={setVisuallyImpaired}
                            setColorblind={setColorblind}
                        />
                    </div>
                </>
            )}
        </div>
    );
}
