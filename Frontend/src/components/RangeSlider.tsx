import type { CSSProperties } from "react";

interface RangeSliderProps {
    readonly label: string;
    readonly value: number;
    readonly displayValue: string;
    readonly min: number;
    readonly max: number;
    readonly step: number;
    readonly onChange: (value: number) => void;
    readonly labelColor: string;
    readonly valueColor: string;
    readonly trackGradient: string;
    readonly containerStyle?: CSSProperties;
    readonly labelRowStyle?: CSSProperties;
    readonly inputStyle?: CSSProperties;
}

export function RangeSlider({
    label,
    value,
    displayValue,
    min,
    max,
    step,
    onChange,
    labelColor,
    valueColor,
    trackGradient,
    containerStyle,
    labelRowStyle,
    inputStyle,
}: RangeSliderProps) {
    return (
        <div style={containerStyle}>
            <label
                style={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                    marginBottom: "10px",
                    fontWeight: "500",
                    color: labelColor,
                    fontSize: "14px",
                    ...labelRowStyle,
                }}
            >
                <span>{label}</span>
                <span style={{ color: valueColor, fontWeight: "700", fontSize: "15px" }}>
                    {displayValue}
                </span>
            </label>
            <input
                type="range"
                min={min}
                max={max}
                step={step}
                value={value}
                onChange={(e) => onChange(Number(e.target.value))}
                style={{
                    width: "100%",
                    height: "6px",
                    borderRadius: "10px",
                    background: trackGradient,
                    outline: "none",
                    cursor: "pointer",
                    appearance: "none",
                    WebkitAppearance: "none",
                    ...inputStyle,
                }}
            />
        </div>
    );
}