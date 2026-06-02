import { useEffect } from "react";
import { MapContainer, Polygon, TileLayer, useMap } from "react-leaflet";
import { MapGridLayer } from "../components/MapGridLayer";
import { MapSidebar } from "../components/MapSidebar";
import { ThemeProvider } from "../components/ThemeContext";
import { useTheme } from "../components/ThemeContext";
import { useMapController } from "../components/mapController";
import { getFilterLabel } from "../components/mapFilters";
import { t } from "../components/i18n";

function TopRightOverlay({
  language,
  mapSources,
  isLoading,
  error,
  hasMapData,
}: {
  language: "pl" | "en";
  mapSources: Array<{
    type: string;
    label: string;
    valueType: string;
    dataProvider: string;
  }>;
  isLoading: boolean;
  error: string | null;
  hasMapData: boolean;
}) {
  const { darkMode, panelTitleColor, panelTextColor, panelBorderColor } =
    useTheme();

  const providerCardStyle: React.CSSProperties = {
    padding: "12px 14px",
    borderRadius: 16,
    background: darkMode
      ? "rgba(17, 24, 39, 0.84)"
      : "rgba(255, 255, 255, 0.84)",
    border: darkMode
      ? "1px solid rgba(255,255,255,0.10)"
      : "1px solid rgba(15,23,42,0.10)",
    color: panelTextColor,
    boxShadow: "0 12px 30px rgba(0, 0, 0, 0.18)",
    backdropFilter: "blur(12px)",
    WebkitBackdropFilter: "blur(12px)",
    maxWidth: 360,
  };

  const statusCardStyle: React.CSSProperties = {
    padding: "12px 16px",
    borderRadius: 12,
    background: error ? "rgba(220, 53, 69, 0.92)" : "rgba(17, 24, 39, 0.82)",
    color: "#fff",
    maxWidth: 320,
    boxShadow: "0 10px 30px rgba(0, 0, 0, 0.25)",
    backdropFilter: "blur(10px)",
  };

  return (
    <div
      style={{
        position: "absolute",
        top: 16,
        right: 16,
        zIndex: 1000,
        display: "flex",
        flexDirection: "column",
        gap: 12,
        alignItems: "flex-end",
      }}
    >
      {mapSources.length > 0 && (
        <div style={providerCardStyle}>
          <div
            style={{
              color: panelTitleColor,
              fontSize: 12,
              fontWeight: 700,
              textTransform: "uppercase",
              letterSpacing: 0.8,
              marginBottom: 8,
            }}
          >
            {t(language, "dataProvider")}
          </div>
          <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
            {mapSources.map((source) => (
              <div
                key={`${source.type}-${source.dataProvider}`}
                style={{
                  padding: "8px 10px",
                  borderRadius: 12,
                  background: darkMode
                    ? "rgba(255,255,255,0.05)"
                    : "rgba(15,23,42,0.04)",
                  border: `1px solid ${panelBorderColor}`,
                }}
              >
                <div
                  style={{
                    fontWeight: 700,
                    color: panelTitleColor,
                    marginBottom: 4,
                  }}
                >
                  {source.label}
                </div>
                <div style={{ fontSize: 12, opacity: 0.8, marginBottom: 4 }}>
                  {source.valueType}
                </div>
                <div style={{ wordBreak: "break-word" }}>
                  {source.dataProvider}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {(isLoading || error || !hasMapData) && (
        <div style={statusCardStyle}>
          {error ??
            (isLoading ? t(language, "loadingMap") : t(language, "noMapData"))}
        </div>
      )}
    </div>
  );
}

function MapResizeObserver() {
  const map = useMap();

  useEffect(() => {
    const container = map.getContainer();
    const observer = new ResizeObserver(() => {
      map.invalidateSize();
    });

    observer.observe(container);

    return () => observer.disconnect();
  }, [map]);

  return null;
}

export default function MapPage() {
  const controller = useMapController();

  return (
    <ThemeProvider
      darkMode={controller.darkMode}
      visuallyImpaired={controller.visuallyImpaired}
    >
      <div
        style={{
          display: "flex",
          height: "100vh",
          fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
        }}
      >
        <MapSidebar
          filters={controller.filters}
          selectedFilter={controller.selectedFilter}
          setSelectedFilter={controller.setSelectedFilter}
          setMinValue={controller.setMinValue}
          setMaxValue={controller.setMaxValue}
          minValue={controller.minValue}
          maxValue={controller.maxValue}
          language={controller.language}
          setLanguage={controller.setLanguage}
          setDarkMode={controller.setDarkMode}
          gridSize={controller.gridSize}
          setGridSize={controller.setGridSize}
          combinedMode={controller.combinedMode}
          setCombinedMode={controller.setCombinedMode}
          selectedFilters={controller.selectedFilters}
          minMaxPerFilter={controller.minMaxPerFilter}
          highContrast={controller.highContrast}
          setHighContrast={controller.setHighContrast}
          setVisuallyImpaired={controller.setVisuallyImpaired}
          colorblind={controller.colorblind}
          setColorblind={controller.setColorblind}
          formattedMinValue={controller.formattedMinValue}
          formattedMaxValue={controller.formattedMaxValue}
          toggleCombinedFilter={controller.toggleCombinedFilter}
          updateCombinedFilterRange={controller.updateCombinedFilterRange}
          isLoading={controller.isLoading}
          error={controller.error}
        />

        <div style={{ flex: 1, minWidth: 0, position: "relative" }}>
          <MapContainer
            center={controller.mapCenter}
            zoom={controller.gridSize}
            scrollWheelZoom
            style={{ height: "100%", width: "100%" }}
          >
            <TileLayer
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            {controller.mapBounds && (
              <Polygon
                positions={[
                  [
                    controller.mapBounds.latitudeLeftBorder,
                    controller.mapBounds.longitudeTopBorder,
                  ],
                  [
                    controller.mapBounds.latitudeRightBorder,
                    controller.mapBounds.longitudeTopBorder,
                  ],
                  [
                    controller.mapBounds.latitudeRightBorder,
                    controller.mapBounds.longitudeBottomBorder,
                  ],
                  [
                    controller.mapBounds.latitudeLeftBorder,
                    controller.mapBounds.longitudeBottomBorder,
                  ],
                ]}
                pathOptions={{ color: "#d81b60", weight: 3, fillOpacity: 0.08 }}
              />
            )}
            <MapGridLayer
              gridCells={controller.gridCells}
              combinedMode={controller.combinedMode}
              selectedFilters={controller.selectedFilters}
              selectedFilter={controller.selectedFilter}
              language={controller.language}
              getFilterLabel={(key) => getFilterLabel(key, controller.language)}
              getCellStyle={controller.getCellStyle}
              getCellPopupValue={controller.getCellPopupValue}
            />
            <MapResizeObserver />
          </MapContainer>
          <TopRightOverlay
            language={controller.language}
            mapSources={controller.mapSources}
            isLoading={controller.isLoading}
            error={controller.error}
            hasMapData={controller.gridCells.length > 0}
          />
        </div>
      </div>
    </ThemeProvider>
  );
}
