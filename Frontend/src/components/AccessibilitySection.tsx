import { useTheme } from "./ThemeContext";
import { t, type Language } from "./i18n";

interface AccessibilitySectionProps {
  readonly language: Language;
  readonly highContrast: boolean;
  readonly colorblind: boolean;
  readonly setHighContrast: (value: boolean) => void;
  readonly setVisuallyImpaired: (value: boolean) => void;
  readonly setColorblind: (value: boolean) => void;
}

export function AccessibilitySection({
  language,
  highContrast,
  colorblind,
  setHighContrast,
  setVisuallyImpaired,
  setColorblind,
}: AccessibilitySectionProps) {
  const { panelTextColor, visuallyImpaired, panelBorderColor } = useTheme();

  return (
    <div
      style={{
        marginTop: "20px",
        paddingTop: "18px",
        borderTop: `1px solid ${panelBorderColor}`,
      }}
    >
      <h5
        style={{
          margin: 0,
          marginBottom: "14px",
          fontSize: "13px",
          fontWeight: "700",
          color: panelTextColor,
          textTransform: "uppercase",
          letterSpacing: "0.8px",
          opacity: 0.8,
        }}
      >
        ♿ {t(language, "accessibility")}
      </h5>
      <div
        style={{
          marginBottom: "14px",
          padding: "10px 12px",
          borderRadius: "8px",
          backgroundColor: highContrast
            ? "rgba(251, 146, 60, 0.1)"
            : "transparent",
          transition: "all 0.2s ease",
        }}
      >
        <label
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            fontWeight: "500",
            color: highContrast ? "#ea580c" : panelTextColor,
            fontSize: visuallyImpaired ? "18px" : "14px",
            cursor: "pointer",
          }}
        >
          <span>☀ {t(language, "highContrast")}</span>
          <input
            type="checkbox"
            checked={highContrast}
            onChange={(e) => setHighContrast(e.target.checked)}
            style={{
              width: visuallyImpaired ? "24px" : "18px",
              height: visuallyImpaired ? "24px" : "18px",
              accentColor: "#0d9488",
              cursor: "pointer",
            }}
          />
        </label>
      </div>
      <div
        style={{
          marginBottom: "14px",
          padding: "10px 12px",
          borderRadius: "8px",
          backgroundColor: visuallyImpaired
            ? "rgba(99, 102, 241, 0.1)"
            : "transparent",
          transition: "all 0.2s ease",
        }}
      >
        <label
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            fontWeight: "500",
            color: visuallyImpaired ? "#4f46e5" : panelTextColor,
            fontSize: visuallyImpaired ? "18px" : "14px",
            cursor: "pointer",
          }}
        >
          <span>👁 {t(language, "visuallyImpaired")}</span>
          <input
            type="checkbox"
            checked={visuallyImpaired}
            onChange={(e) => setVisuallyImpaired(e.target.checked)}
            style={{
              width: visuallyImpaired ? "24px" : "18px",
              height: visuallyImpaired ? "24px" : "18px",
              accentColor: "#0d9488",
              cursor: "pointer",
            }}
          />
        </label>
      </div>
      <div
        style={{
          padding: "10px 12px",
          borderRadius: "8px",
          backgroundColor: colorblind
            ? "rgba(168, 85, 247, 0.1)"
            : "transparent",
          transition: "all 0.2s ease",
        }}
      >
        <label
          style={{
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            fontWeight: "500",
            color: colorblind ? "#a855f7" : panelTextColor,
            fontSize: visuallyImpaired ? "18px" : "14px",
            cursor: "pointer",
          }}
        >
          <span>🎨 {t(language, "colorblind")}</span>
          <input
            type="checkbox"
            checked={colorblind}
            onChange={(e) => setColorblind(e.target.checked)}
            style={{
              width: visuallyImpaired ? "24px" : "18px",
              height: visuallyImpaired ? "24px" : "18px",
              accentColor: "#0d9488",
              cursor: "pointer",
            }}
          />
        </label>
      </div>
    </div>
  );
}
