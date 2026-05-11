import { useEffect } from "react";
import { MapContainer, Polygon, TileLayer, useMap } from "react-leaflet";
import { MapGridLayer } from "../components/MapGridLayer";
import { MapSidebar } from "../components/MapSidebar";
import { ThemeProvider } from "../components/ThemeContext";
import { KRAKOW, KRAKOW_BORDER, useMapController } from "../components/mapController";
import { filters, type FilterKey } from "../components/mapFilters";

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

    const getFilterLabel = (key: FilterKey) => {
        return filters.find((filter) => filter.key === key)?.label ?? key;
    };

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
                    selectedFilter={controller.selectedFilter}
                    setSelectedFilter={controller.setSelectedFilter}
                    setMinValue={controller.setMinValue}
                    setMaxValue={controller.setMaxValue}
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
                />

                <div style={{ flex: 1, minWidth: 0 }}>
                    <MapContainer
                        center={KRAKOW}
                        zoom={13}
                        scrollWheelZoom
                        style={{ height: "100%", width: "100%" }}
                    >
                        <TileLayer
                            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                        />
                        <Polygon
                            positions={KRAKOW_BORDER}
                            pathOptions={{ color: "#d81b60", weight: 3, fillOpacity: 0.08 }}
                        />
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
                </div>
            </div>
        </ThemeProvider>
    );
}
