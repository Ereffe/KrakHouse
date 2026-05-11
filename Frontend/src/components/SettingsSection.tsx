interface SettingsSectionProps {
    readonly language: "pl" | "en";
    readonly darkMode: boolean;
    readonly gridSize: number;
    readonly panelTextColor: string;
    readonly panelMutedColor: string;
    readonly subTitleFontSize: string;
    readonly setLanguage: (value: "pl" | "en") => void;
    readonly setDarkMode: (value: boolean) => void;
    readonly setGridSize: (value: number) => void;
}

export function SettingsSection({
    language,
    darkMode,
    gridSize,
    panelTextColor,
    panelMutedColor,
    subTitleFontSize,
    setLanguage,
    setDarkMode,
    setGridSize,
}: SettingsSectionProps) {
    return (
        <div>
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
        </div>
    );
}
