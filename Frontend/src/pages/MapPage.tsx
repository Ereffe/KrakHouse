import { MapContainer, Marker, Polygon, Popup, TileLayer } from "react-leaflet";
import { MapGridLayer } from "./MapGridLayer";
import { MapSidebar } from "./MapSidebar";
import { KRAKOW, KRAKOW_BORDER, useMapController } from "./mapController";
import { filters, type FilterKey } from "./mapFilters";

export default function MapPage() {
    const controller = useMapController();

    const getFilterLabel = (key: FilterKey) => {
        return filters.find((filter) => filter.key === key)?.label ?? key;
    };

    return (
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
                minValue={controller.minValue}
                setMinValue={controller.setMinValue}
                maxValue={controller.maxValue}
                setMaxValue={controller.setMaxValue}
                language={controller.language}
                setLanguage={controller.setLanguage}
                darkMode={controller.darkMode}
                setDarkMode={controller.setDarkMode}
                gridSize={controller.gridSize}
                setGridSize={controller.setGridSize}
                combinedMode={controller.combinedMode}
                setCombinedMode={controller.setCombinedMode}
                selectedFilters={controller.selectedFilters}
                minMaxPerFilter={controller.minMaxPerFilter}
                highContrast={controller.highContrast}
                setHighContrast={controller.setHighContrast}
                visuallyImpaired={controller.visuallyImpaired}
                setVisuallyImpaired={controller.setVisuallyImpaired}
                colorblind={controller.colorblind}
                setColorblind={controller.setColorblind}
                panelTitleColor={controller.panelTitleColor}
                panelTextColor={controller.panelTextColor}
                panelMutedColor={controller.panelMutedColor}
                baseFontSize={controller.baseFontSize}
                titleFontSize={controller.titleFontSize}
                subTitleFontSize={controller.subTitleFontSize}
                formattedMinValue={controller.formattedMinValue}
                formattedMaxValue={controller.formattedMaxValue}
                toggleCombinedFilter={controller.toggleCombinedFilter}
                updateCombinedFilterRange={controller.updateCombinedFilterRange}
            />

            <div style={{ flex: 1 }}>
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
                    <Marker position={KRAKOW}>
                        <Popup>Krakow, Poland</Popup>
                    </Marker>
                </MapContainer>
            </div>
        </div>
    );
}
