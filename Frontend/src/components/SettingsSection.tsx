import { RangeSlider } from "./RangeSlider";
import { useTheme } from "./ThemeContext";

interface SettingsSectionProps {
    readonly language: "pl" | "en";
    readonly gridSize: number;
    readonly setLanguage: (value: "pl" | "en") => void;
    readonly setDarkMode: (value: boolean) => void;
    readonly setGridSize: (value: number) => void;
}

export function SettingsSection({
    language,
    gridSize,
    setLanguage,
    setDarkMode,
    setGridSize,
}: SettingsSectionProps) {
    const { darkMode, panelTextColor, panelMutedColor } = useTheme();

    return (
        <div>
            <h4
                style={{
                    margin: 0,
                    marginBottom: "18px",
                    fontSize: "13px",
                    fontWeight: "700",
                    color: darkMode ? "#cbd5e0" : "#4b5563",
                    textTransform: "uppercase",
                    letterSpacing: "0.8px",
                }}
            >
                ⚙ Ustawienia
            </h4>
            <RangeSlider
                label="Poziom przybliżenia"
                value={gridSize}
                displayValue={`${gridSize}`}
                min={5}
                max={20}
                step={1}
                onChange={setGridSize}
                labelColor={panelTextColor}
                valueColor="#8b5cf6"
                trackGradient="linear-gradient(to right, #8b5cf6, #d946ef)"
                containerStyle={{ marginBottom: "18px" }}
            />
            <div style={{ marginBottom: "18px" }}>
                <div
                    style={{
                        display: "block",
                        marginBottom: "10px",
                        fontWeight: "500",
                        color: panelTextColor,
                        fontSize: "14px",
                    }}
                >
                    🌐 Język strony
                </div>
                <select
                    value={language}
                    onChange={(e) => setLanguage(e.target.value as "pl" | "en")}
                    style={{
                        width: "100%",
                        padding: "10px 12px",
                        borderRadius: "8px",
                        border: "1.5px solid transparent",
                        backgroundColor: darkMode ? "#2d3748" : "#f7f9fc",
                        color: darkMode ? "#e2e8f0" : "#2d3748",
                        cursor: "pointer",
                        fontSize: "14px",
                        fontWeight: "500",
                        transition: "all 0.2s ease",
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
                        marginBottom: "8px",
                        fontSize: "14px",
                    }}
                >
                    <span>🌙 Tryb ciemny</span>
                    <input
                        type="checkbox"
                        checked={darkMode}
                        onChange={(e) => setDarkMode(e.target.checked)}
                        style={{
                            width: "18px",
                            height: "18px",
                            accentColor: "#0d9488",
                            cursor: "pointer",
                        }}
                    />
                </label>
                <p
                    style={{
                        margin: 0,
                        marginTop: "6px",
                        fontSize: "12px",
                        color: panelMutedColor,
                        opacity: 0.7,
                    }}
                >
                    Włącz dla wygody oka w nocy
                </p>
            </div>
        </div>
    );
}
