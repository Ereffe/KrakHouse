import { useEffect } from "react";
import { MapContainer, Polygon, TileLayer, useMap } from "react-leaflet";
import { MapGridLayer } from "../components/MapGridLayer";
import { MapSidebar } from "../components/MapSidebar";
import { ThemeProvider } from "../components/ThemeContext";
import { useMapController } from "../components/mapController";
import { getFilterLabel } from "../components/mapFilters";

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
        <ThemeProvider darkMode={controller.darkMode} visuallyImpaired={controller.visuallyImpaired}>
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
                                    [controller.mapBounds.latitudeLeftBorder, controller.mapBounds.longitudeTopBorder],
                                    [controller.mapBounds.latitudeRightBorder, controller.mapBounds.longitudeTopBorder],
                                    [controller.mapBounds.latitudeRightBorder, controller.mapBounds.longitudeBottomBorder],
                                    [controller.mapBounds.latitudeLeftBorder, controller.mapBounds.longitudeBottomBorder],
                                ]}
                                pathOptions={{ color: "#d81b60", weight: 3, fillOpacity: 0.08 }}
                            />
                        )}
                        <MapGridLayer
                            gridCells={controller.gridCells}
                            combinedMode={controller.combinedMode}
                            selectedFilters={controller.selectedFilters}
                            selectedFilter={controller.selectedFilter}
                            getFilterLabel={getFilterLabel}
                            getCellStyle={controller.getCellStyle}
                            getCellPopupValue={controller.getCellPopupValue}
                        />
                        <MapResizeObserver />

                    </MapContainer>
                    {(controller.isLoading || controller.error || controller.gridCells.length === 0) && (
                        <div
                            style={{
                                position: "absolute",
                                top: 16,
                                right: 16,
                                zIndex: 1000,
                                padding: "12px 16px",
                                borderRadius: "12px",
                                background: controller.error
                                    ? "rgba(220, 53, 69, 0.9)"
                                    : "rgba(17, 24, 39, 0.82)",
                                color: "#fff",
                                maxWidth: "320px",
                                boxShadow: "0 10px 30px rgba(0, 0, 0, 0.25)",
                                backdropFilter: "blur(10px)",
                            }}
                        >
                            {controller.error ?? (controller.isLoading ? "Pobieranie danych z backendu..." : "Brak danych do wyświetlenia.")}
                        </div>
                    )}
                </div>
            </div>
        </ThemeProvider>
    );
}
