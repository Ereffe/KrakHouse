import { useMemo, useState } from "react";
import { AccessibilitySection } from "./AccessibilitySection";
import { CombinedFilterSection } from "./CombinedFilterSection";
import { SingleFilterSection } from "./SingleFilterSection";
import { SettingsSection } from "./SettingsSection";
import { useTheme } from "./ThemeContext";
import { t, type Language } from "./i18n";
import {
  getFrontendFilterRange,
  type FilterDefinition,
  type FilterKey,
} from "./mapFilters";

interface MapSidebarProps {
  filters: FilterDefinition[];
  selectedFilter: FilterKey;
  setSelectedFilter: (value: FilterKey) => void;
  setMinValue: (value: number) => void;
  setMaxValue: (value: number) => void;
  language: Language;
  setLanguage: (value: Language) => void;
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
  minValue: number;
  maxValue: number;
  toggleCombinedFilter: (filterKey: FilterKey, checked: boolean) => void;
  updateCombinedFilterRange: (
    filterKey: FilterKey,
    rangeType: "min" | "max",
    value: number,
  ) => void;
  isLoading: boolean;
  error: string | null;
}

export function MapSidebar({
  filters,
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
  minValue,
  maxValue,
  toggleCombinedFilter,
  updateCombinedFilterRange,
  isLoading,
  error,
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

  const selectedBackendFilter = useMemo(
    () => filters.find((filter) => filter.key === selectedFilter) ?? filters[0],
    [filters, selectedFilter],
  );

  const selectedFilterConfig = useMemo(() => {
    if (!selectedBackendFilter) return undefined;
    return getFrontendFilterRange(selectedBackendFilter);
  }, [selectedBackendFilter]);

  const collapsedWidth = 58;
  const expandedWidth = 320;

  const glassPanelStyle: React.CSSProperties = {
    width: isCollapsed ? `${collapsedWidth}px` : `${expandedWidth}px`,
    padding: isCollapsed ? "14px 7px" : "18px 18px",
    background: sidebarBackground,
    borderRight: "none",
    overflowY: "auto",
    overflowX: "hidden",
    backdropFilter: "blur(12px)",
    WebkitBackdropFilter: "blur(12px)",
    boxShadow: darkMode
      ? "0 18px 60px rgba(0,0,0,0.55)"
      : "0 18px 60px rgba(2,6,23,0.18)",
    borderRadius: 18,
    border: darkMode
      ? "1px solid rgba(255,255,255,0.08)"
      : "1px solid rgba(15,23,42,0.10)",
    transition: "width 0.26s ease, padding 0.26s ease",
    display: "flex",
    flexDirection: "column",
    color: panelTextColor,
    position: "absolute",
    left: 16,
    top: 16,
    bottom: 16,
    height: "auto",
    zIndex: 1001,
    scrollbarWidth: "thin",
    scrollbarColor: darkMode
      ? "rgba(255,255,255,0.35) transparent"
      : "rgba(15,23,42,0.35) transparent",
  };

  const collapsedLabelStyle: React.CSSProperties = {
    marginTop: 6,
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    height: 54,
    userSelect: "none",
    color: panelTitleColor,
    fontWeight: 800,
    letterSpacing: 1,
    fontSize: 12,
    opacity: 0.9,
  };

  const collapseButtonStyle: React.CSSProperties = {
    width: "44px",
    height: "44px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background: darkMode ? "rgba(255,255,255,0.06)" : "rgba(255,255,255,0.55)",
    border: darkMode ? `1px solid rgba(255,255,255,0.12)` : "1px solid #e5e7eb",
    borderRadius: "12px",
    color: darkMode ? "#e2e8f0" : "#0f172a",
    cursor: "pointer",
    fontSize: "18px",
    fontWeight: 700,
    transition:
      "transform 0.12s ease, background 0.18s ease, border-color 0.18s ease",
    marginBottom: "14px",
    marginLeft: isCollapsed ? "0.25rem" : 0,
    flexShrink: 0,
    boxShadow: darkMode
      ? "0 10px 20px rgba(0,0,0,0.25)"
      : "0 10px 20px rgba(2,6,23,0.08)",
  };

  const cardStyle = (_accent: string, subtleBg: string): React.CSSProperties =>
    ({
      borderRadius: 16,
      border: darkMode
        ? `1px solid rgba(255,255,255,0.08)`
        : `1px solid rgba(15,23,42,0.08)`,
      background: subtleBg,
      boxShadow: darkMode
        ? "0 18px 45px rgba(0,0,0,0.24)"
        : "0 18px 45px rgba(2,6,23,0.10)",
      overflow: "hidden",
      padding: 14,
    }) as React.CSSProperties;

  const dividerStyle: React.CSSProperties = {
    height: 1,
    background: panelBorderColor,
    opacity: 0.65,
    margin: "14px 0",
  };

  return (
    <aside style={glassPanelStyle}>
      <button
        onClick={() => setIsCollapsed((v) => !v)}
        style={collapseButtonStyle}
        title={
          isCollapsed
            ? t(language, "expandSidebar")
            : t(language, "collapseSidebar")
        }
        onMouseDown={(e) => {
          (e.currentTarget as HTMLButtonElement).style.transform =
            "scale(0.98)";
        }}
        onMouseUp={(e) => {
          (e.currentTarget as HTMLButtonElement).style.transform = "scale(1)";
        }}
      >
        {isCollapsed ? "›" : "‹"}
      </button>

      {isCollapsed && (
        <div style={collapsedLabelStyle}>
          <span
            style={{ writingMode: "vertical-rl", transform: "rotate(180deg)" }}
          >
            {t(language, "filters")}
          </span>
        </div>
      )}

      {!isCollapsed && (
        <div style={{ paddingRight: 6 }}>
          {/* Header */}
          <div style={{ display: "flex", flexDirection: "column", gap: 10 }}>
            <div
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                gap: 12,
              }}
            >
              <h3
                style={{
                  margin: 0,
                  fontSize: titleFontSize,
                  color: panelTitleColor,
                  fontWeight: 700,
                  letterSpacing: -0.2,
                }}
              >
                {t(language, "filters")}
              </h3>
            </div>
            <div style={dividerStyle} />
          </div>

          {/* Loading/Error */}
          {(isLoading || error) && (
            <div
              style={cardStyle(
                "#0d9488",
                error
                  ? darkMode
                    ? "rgba(220,38,38,0.18)"
                    : "rgba(220,38,38,0.12)"
                  : darkMode
                    ? "rgba(13,148,136,0.16)"
                    : "rgba(13,148,136,0.10)",
              )}
            >
              <div
                style={{
                  fontSize: 13,
                  lineHeight: 1.5,
                  color: error ? "#ef4444" : darkMode ? "#99f6e4" : "#0f766e",
                  fontWeight: 600,
                }}
              >
                {error ?? t(language, "loadingFilters")}
              </div>
            </div>
          )}

          {/* Combined mode toggle */}
          <div
            style={cardStyle(
              "#0d9488",
              darkMode ? "rgba(2,132,199,0.06)" : "rgba(13,148,136,0.06)",
            )}
          >
            <label
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                gap: 12,
                cursor: "pointer",
                userSelect: "none",
              }}
            >
              <span
                style={{
                  fontSize: baseFontSize,
                  fontWeight: 600,
                  color: panelTextColor,
                }}
              >
                {t(language, "combinedFiltersMode")}
              </span>
              <input
                type="checkbox"
                checked={combinedMode}
                onChange={(e) => setCombinedMode(e.target.checked)}
                style={{
                  width: 18,
                  height: 18,
                  accentColor: "#0d9488",
                  cursor: "pointer",
                }}
              />
            </label>
          </div>

          {/* Filters content */}
          <div style={{ marginTop: 14 }}>
            {combinedMode ? (
              <div>
                <CombinedFilterSection
                  language={language}
                  filters={filters}
                  selectedFilters={selectedFilters}
                  minMaxPerFilter={minMaxPerFilter}
                  toggleCombinedFilter={toggleCombinedFilter}
                  updateCombinedFilterRange={updateCombinedFilterRange}
                />

                {selectedFilters.length > 0 && (
                  <div style={{ marginTop: 16 }}>
                    <div
                      style={{
                        fontSize: 13,
                        fontWeight: 800,
                        color: panelTitleColor,
                        textTransform: "uppercase",
                        letterSpacing: 0.8,
                        opacity: 0.85,
                        marginBottom: 10,
                      }}
                    >
                      {t(language, "selectedFilters")}
                    </div>
                    <div
                      style={{
                        display: "flex",
                        flexDirection: "column",
                        gap: 8,
                      }}
                    >
                      {selectedFilters.map((filterKey) => (
                        <div
                          key={filterKey}
                          style={{
                            padding: "10px 12px",
                            borderRadius: 12,
                            background: darkMode
                              ? "rgba(13,148,136,0.12)"
                              : "rgba(13,148,136,0.08)",
                            border: darkMode
                              ? "1px solid rgba(13,148,136,0.20)"
                              : "1px solid rgba(13,148,136,0.18)",
                            color: "#0d9488",
                            fontWeight: 700,
                            fontSize: 14,
                          }}
                        >
                          {
                            filters.find((filter) => filter.key === filterKey)
                              ?.label
                          }
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <div>
                <SingleFilterSection
                  language={language}
                  filters={filters}
                  selectedFilter={selectedFilter}
                  selectedFilterConfig={selectedFilterConfig}
                  minValue={minValue}
                  maxValue={maxValue}
                  formattedMinValue={formattedMinValue}
                  formattedMaxValue={formattedMaxValue}
                  setSelectedFilter={setSelectedFilter}
                  setMinValue={setMinValue}
                  setMaxValue={setMaxValue}
                />
              </div>
            )}
          </div>

          {/* Settings + Accessibility */}
          <div style={{ marginTop: 18 }}>
            <div style={dividerStyle} />
            <div style={{ display: "flex", flexDirection: "column", gap: 14 }}>
              <div
                style={{
                  borderRadius: 16,
                  border: darkMode
                    ? "1px solid rgba(255,255,255,0.08)"
                    : "1px solid rgba(15,23,42,0.08)",
                  background: darkMode
                    ? "rgba(255,255,255,0.03)"
                    : "rgba(255,255,255,0.45)",
                  padding: 14,
                }}
              >
                <SettingsSection
                  language={language}
                  gridSize={gridSize}
                  setLanguage={setLanguage}
                  setDarkMode={setDarkMode}
                  setGridSize={setGridSize}
                />
              </div>

              <div
                style={{
                  borderRadius: 16,
                  border: darkMode
                    ? "1px solid rgba(255,255,255,0.08)"
                    : "1px solid rgba(15,23,42,0.08)",
                  background: darkMode
                    ? "rgba(255,255,255,0.03)"
                    : "rgba(255,255,255,0.45)",
                  padding: 14,
                }}
              >
                <AccessibilitySection
                  language={language}
                  highContrast={highContrast}
                  colorblind={colorblind}
                  setHighContrast={setHighContrast}
                  setVisuallyImpaired={setVisuallyImpaired}
                  setColorblind={setColorblind}
                />
              </div>
            </div>
          </div>
        </div>
      )}
    </aside>
  );
}
