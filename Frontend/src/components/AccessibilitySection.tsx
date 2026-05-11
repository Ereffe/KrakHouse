interface AccessibilitySectionProps {
    readonly highContrast: boolean;
    readonly visuallyImpaired: boolean;
    readonly colorblind: boolean;
    readonly panelTextColor: string;
    readonly visuallyImpairedFontSize: string;
    readonly setHighContrast: (value: boolean) => void;
    readonly setVisuallyImpaired: (value: boolean) => void;
    readonly setColorblind: (value: boolean) => void;
}

export function AccessibilitySection({
    highContrast,
    visuallyImpaired,
    colorblind,
    panelTextColor,
    visuallyImpairedFontSize,
    setHighContrast,
    setVisuallyImpaired,
    setColorblind,
}: AccessibilitySectionProps) {
    return (
        <div style={{ marginTop: "20px" }}>
            <h5
                style={{
                    margin: 0,
                    marginBottom: "12px",
                    fontSize: visuallyImpairedFontSize,
                    fontWeight: "600",
                    color: panelTextColor,
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
    );
}
