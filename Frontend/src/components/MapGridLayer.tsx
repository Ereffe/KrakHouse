import { Polygon, Popup } from "react-leaflet";
import type { GridCell } from "./mapController";
import type { FilterKey } from "./mapFilters";

interface MapGridLayerProps {
    gridCells: GridCell[];
    combinedMode: boolean;
    selectedFilters: FilterKey[];
    selectedFilter: FilterKey;
    getFilterLabel: (key: FilterKey) => string;
    getCellStyle: (cell: GridCell) => { color: string; fillOpacity: number } | null;
    getCellPopupValue: (cell: GridCell, filterKey: FilterKey) => string;
}

export function MapGridLayer({
    gridCells,
    combinedMode,
    selectedFilters,
    selectedFilter,
    getFilterLabel,
    getCellStyle,
    getCellPopupValue,
}: Readonly<MapGridLayerProps>) {
    return (
        <>
            {gridCells.map((cell) => {
                const style = getCellStyle(cell);
                if (!style) return null;

                return (
                    <Polygon
                        key={cell.id}
                        positions={cell.positions}
                        pathOptions={{
                            color: style.color,
                            weight: 1,
                            fillOpacity: style.fillOpacity,
                        }}
                    >
                        <Popup>
                            Cell {cell.id}
                            <br />
                            {combinedMode ? (
                                selectedFilters.map((filterKey) => (
                                    <div key={filterKey}>
                                        {getFilterLabel(filterKey)}: {getCellPopupValue(cell, filterKey)}
                                    </div>
                                ))
                            ) : (
                                <>
                                    {getFilterLabel(selectedFilter)}: {getCellPopupValue(cell, selectedFilter)}
                                </>
                            )}
                        </Popup>
                    </Polygon>
                );
            })}
        </>
    );
}
