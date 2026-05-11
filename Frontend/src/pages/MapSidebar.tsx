import { filters, type FilterKey } from "./mapFilters";

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
                                        color: selectedFilters.includes(filter.key)
                                            ? "#28a745"
                                            : panelTextColor,
                                        fontWeight: selectedFilters.includes(filter.key)
                                            ? "700"
                                            : "500",
                                    }}
                                >
                                    <input
                                        type="checkbox"
                                        checked={selectedFilters.includes(filter.key)}
                                        onChange={(e) =>
                                            toggleCombinedFilter(filter.key, e.target.checked)
                                        }
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
                                                    updateCombinedFilterRange(
                                                        filter.key,
                                                        "min",
                                                        Number(e.target.value),
                                                    )
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
                                                    updateCombinedFilterRange(
                                                        filter.key,
                                                        "max",
                                                        Number(e.target.value),
                                                    )
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
                                        color:
                                            selectedFilter === filter.key ? "#28a745" : panelTextColor,
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
                            <span style={{ color: "#dc3545", fontWeight: "bold" }}>
                                {formattedMinValue}
                            </span>
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
                                background:
                                    "linear-gradient(to right, #dc3545, #ffc107, #28a745)",
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
                            <span style={{ color: "#28a745", fontWeight: "bold" }}>
                                {formattedMaxValue}
                            </span>
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
                                background:
                                    "linear-gradient(to right, #dc3545, #ffc107, #28a745)",
                                outline: "none",
                                cursor: "pointer",
                                appearance: "none",
                                WebkitAppearance: "none",
                            }}
                        />
                    </div>
                </>
            )}
            <div
                style={{
                    marginTop: "30px",
                    paddingTop: "20px",
                    borderTop: darkMode ? "1px solid #495057" : "1px solid #ced4da",
                }}
            >
                <h4
                    style={{
                        margin: 0,
                        marginBottom: "16px",
                        fontSize: subTitleFontSize,
                        fontWeight: "600",
                        color: darkMode ? "#f8f9fa" : "#495057",
                    }}
                >
                    Ustawienia
                </h4>
                <div style={{ marginBottom: "16px" }}>
                    <label
                        style={{
                            display: "block",
                            marginBottom: "8px",
                            fontWeight: "500",
                            color: panelTextColor,
                        }}
                    >
                        Rozmiar siatki: {gridSize} x {gridSize}
                    </label>
                    <input
                        type="range"
                        min={5}
                        max={20}
                        step={1}
                        value={gridSize}
                        onChange={(e) => setGridSize(Number(e.target.value))}
                        style={{
                            width: "100%",
                            height: "8px",
                            borderRadius: "10px",
                            background: "linear-gradient(to right, #6f42c1, #6610f2)",
                            outline: "none",
                            cursor: "pointer",
                            appearance: "none",
                            WebkitAppearance: "none",
                        }}
                    />
                </div>
                <div style={{ marginBottom: "16px" }}>
                    <label
                        style={{
                            display: "block",
                            marginBottom: "8px",
                            fontWeight: "500",
                            color: panelTextColor,
                        }}
                    >
                        Język strony
                    </label>
                    <select
                        value={language}
                        onChange={(e) => setLanguage(e.target.value as "pl" | "en")}
                        style={{
                            width: "100%",
                            padding: "10px 14px",
                            borderRadius: "10px",
                            border: "1px solid #ced4da",
                            backgroundColor: darkMode ? "#343a40" : "#ffffff",
                            color: darkMode ? "#f8f9fa" : "#495057",
                            cursor: "pointer",
                            fontSize: "14px",
                        }}
                    >
                        <option value="pl">Polski</option>
                        <option value="en">English</option>
                    </select>
                </div>
                <div>
                    <label
                        style={{
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "space-between",
                            fontWeight: "500",
                            color: panelTextColor,
                            marginBottom: "10px",
                        }}
                    >
                        Tryb ciemny
                        <input
                            type="checkbox"
                            checked={darkMode}
                            onChange={(e) => setDarkMode(e.target.checked)}
                            style={{
                                width: "18px",
                                height: "18px",
                                accentColor: "#28a745",
                                cursor: "pointer",
                            }}
                        />
                    </label>
                    <p
                        style={{
                            margin: 0,
                            fontSize: "13px",
                            color: panelMutedColor,
                        }}
                    >
                        Przykładowe ustawienia interfejsu.
                    </p>
                </div>
                <div style={{ marginTop: "20px" }}>
                    <h5
                        style={{
                            margin: 0,
                            marginBottom: "12px",
                            fontSize: visuallyImpaired ? "26px" : "16px",
                            fontWeight: "600",
                            color: darkMode ? "#f8f9fa" : "#495057",
                        }}
                    >
                        Dostępność
                    </h5>
                    <div style={{ marginBottom: "12px" }}>
                        <label
                            style={{
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "space-between",
                                fontWeight: "500",
                                color: panelTextColor,
                                fontSize: visuallyImpaired ? "20px" : "14px",
                            }}
                        >
                            🔆 Wysoki kontrast
                            <input
                                type="checkbox"
                                checked={highContrast}
                                onChange={(e) => setHighContrast(e.target.checked)}
                                style={{
                                    width: visuallyImpaired ? "24px" : "18px",
                                    height: visuallyImpaired ? "24px" : "18px",
                                    accentColor: "#28a745",
                                    cursor: "pointer",
                                }}
                            />
                        </label>
                    </div>
                    <div style={{ marginBottom: "12px" }}>
                        <label
                            style={{
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "space-between",
                                fontWeight: "500",
                                color: panelTextColor,
                                fontSize: visuallyImpaired ? "20px" : "14px",
                            }}
                        >
                            👁️ Dla słabo widzących
                            <input
                                type="checkbox"
                                checked={visuallyImpaired}
                                onChange={(e) => setVisuallyImpaired(e.target.checked)}
                                style={{
                                    width: visuallyImpaired ? "24px" : "18px",
                                    height: visuallyImpaired ? "24px" : "18px",
                                    accentColor: "#28a745",
                                    cursor: "pointer",
                                }}
                            />
                        </label>
                    </div>
                    <div>
                        <label
                            style={{
                                display: "flex",
                                alignItems: "center",
                                justifyContent: "space-between",
                                fontWeight: "500",
                                color: panelTextColor,
                                fontSize: visuallyImpaired ? "20px" : "14px",
                            }}
                        >
                            🌈 Dla daltonistów
                            <input
                                type="checkbox"
                                checked={colorblind}
                                onChange={(e) => setColorblind(e.target.checked)}
                                style={{
                                    width: visuallyImpaired ? "24px" : "18px",
                                    height: visuallyImpaired ? "24px" : "18px",
                                    accentColor: "#28a745",
                                    cursor: "pointer",
                                }}
                            />
                        </label>
                    </div>
                </div>
            </div>
        </div>
    );
}
