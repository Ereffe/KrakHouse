import { useEffect, useMemo, useState } from "react";
import { filters, type FilterKey } from "./mapFilters";

export interface GridCell {
    id: string;
    positions: [number, number][];
    lifeScore: number;
    price: number;
    pollution: number;
    noise: number;
    crimeLevel: number;
}

type MinMax = { min: number; max: number };
type MinMaxPerFilter = Record<FilterKey, MinMax>;

export const KRAKOW: [number, number] = [50.0647, 19.945];
export const KRAKOW_BORDER: [number, number][] = [
    [50.124, 19.79],
    [50.153, 19.86],
    [50.165, 19.96],
    [50.141, 20.03],
    [50.104, 20.08],
    [50.053, 20.107],
    [49.999, 20.083],
    [49.965, 20.023],
    [49.948, 19.953],
    [49.961, 19.892],
    [49.994, 19.832],
    [50.051, 19.79],
    [50.102, 19.776],
];

const minLat = 49.948;
const maxLat = 50.165;
const minLon = 19.776;
const maxLon = 20.107;
const initialGridSize = 10;

function getColor(
    value: number,
    min: number,
    max: number,
    highContrast: boolean,
    colorblind: boolean,
) {
    if (highContrast) {
        if (value <= min) return "#000000";
        if (value >= max) return "#FFFFFF";
        const normalized = (value - min) / (max - min);
        return normalized < 0.5 ? "#000000" : "#FFFFFF";
    }

    if (colorblind) {
        if (value <= min) return "#1f77b4";
        if (value >= max) return "#ff7f0e";
        const normalized = (value - min) / (max - min);
        const r = Math.floor(255 * normalized * 0.8 + 31);
        const g = Math.floor(119 * (1 - normalized) + 127 * normalized);
        const b = Math.floor(180 * (1 - normalized) + 14 * normalized);
        return `rgb(${r}, ${g}, ${b})`;
    }

    if (value <= min) return "#dc3545";
    if (value >= max) return "#28a745";

    const normalized = (value - min) / (max - min);
    const r = Math.floor(255 * (1 - normalized));
    const g = Math.floor(255 * normalized);
    return `rgb(${r}, ${g}, 0)`;
}

function createInitialMinMaxPerFilter(): MinMaxPerFilter {
    return filters.reduce((acc, filter) => {
        acc[filter.key] = { min: filter.min, max: filter.max };
        return acc;
    }, {} as MinMaxPerFilter);
}

function buildGridCells(gridSize: number): GridCell[] {
    const cells: GridCell[] = [];
    const latStep = (maxLat - minLat) / gridSize;
    const lonStep = (maxLon - minLon) / gridSize;

    for (let i = 0; i < gridSize; i++) {
        for (let j = 0; j < gridSize; j++) {
            const lat1 = minLat + i * latStep;
            const lat2 = minLat + (i + 1) * latStep;
            const lon1 = minLon + j * lonStep;
            const lon2 = minLon + (j + 1) * lonStep;

            cells.push({
                id: `${i}-${j}`,
                positions: [
                    [lat1, lon1],
                    [lat2, lon1],
                    [lat2, lon2],
                    [lat1, lon2],
                ],
                lifeScore: Math.floor(Math.random() * 101),
                price: Math.floor(Math.random() * 501) + 10,
                pollution: Math.floor(Math.random() * 10) + 1,
                noise: Math.floor(Math.random() * 11),
                crimeLevel: Math.floor(Math.random() * 11),
            });
        }
    }

    return cells;
}

function formatFilterValue(filter: FilterKey, value: number) {
    if (filter === "price") return `${value} PLN`;
    if (filter === "lifeScore") return `${value}%`;
    return String(value);
}

export function useMapController() {
    const [selectedFilter, setSelectedFilter] = useState<FilterKey>("lifeScore");
    const [minValue, setMinValue] = useState(0);
    const [maxValue, setMaxValue] = useState(10);
    const [language, setLanguage] = useState<"pl" | "en">("pl");
    const [darkMode, setDarkMode] = useState(false);
    const [gridSize, setGridSize] = useState(initialGridSize);
    const [combinedMode, setCombinedMode] = useState(false);
    const [selectedFilters, setSelectedFilters] = useState<FilterKey[]>([]);
    const [minMaxPerFilter, setMinMaxPerFilter] = useState<MinMaxPerFilter>(() =>
        createInitialMinMaxPerFilter(),
    );
    const [highContrast, setHighContrast] = useState(false);
    const [visuallyImpaired, setVisuallyImpaired] = useState(false);
    const [colorblind, setColorblind] = useState(false);

    const panelTitleColor = darkMode ? "#f8f9fa" : "#495057";
    const panelTextColor = darkMode ? "#f8f9fa" : "#343a40";
    const panelMutedColor = darkMode ? "#ced4da" : "#6c757d";
    const baseFontSize = visuallyImpaired ? "20px" : "14px";
    const titleFontSize = visuallyImpaired ? "32px" : "22px";
    const subTitleFontSize = visuallyImpaired ? "24px" : "18px";

    const gridCells = useMemo(() => buildGridCells(gridSize), [gridSize]);

    useEffect(() => {
        const currentFilter = filters.find((filter) => filter.key === selectedFilter);
        if (!currentFilter) return;

        setMinValue(currentFilter.min);
        setMaxValue(currentFilter.max);
    }, [selectedFilter]);

    const formattedMinValue = formatFilterValue(selectedFilter, minValue);
    const formattedMaxValue = formatFilterValue(selectedFilter, maxValue);

    function toggleCombinedFilter(filterKey: FilterKey, checked: boolean) {
        if (checked) {
            setSelectedFilters((prev) => [...prev, filterKey]);
            return;
        }

        setSelectedFilters((prev) => prev.filter((key) => key !== filterKey));
    }

    function updateCombinedFilterRange(
        filterKey: FilterKey,
        rangeType: "min" | "max",
        value: number,
    ) {
        setMinMaxPerFilter((prev) => ({
            ...prev,
            [filterKey]: {
                ...prev[filterKey],
                [rangeType]: value,
            },
        }));
    }

    function getCombinedModeColor() {
        if (highContrast) return "#FFFFFF";
        if (colorblind) return "#ff7f0e";
        return "#28a745";
    }

    function getCellStyle(cell: GridCell): { color: string; fillOpacity: number } | null {
        if (combinedMode) {
            const meetsAllSelected =
                selectedFilters.length > 0 &&
                selectedFilters.every((filterKey) => {
                    const value = cell[filterKey];
                    const { min, max } = minMaxPerFilter[filterKey];
                    return value >= min && value <= max;
                });

            if (!meetsAllSelected) return null;

            return {
                color: getCombinedModeColor(),
                fillOpacity: 0.7,
            };
        }

        const selectedValue = cell[selectedFilter];
        if (selectedValue < minValue || selectedValue > maxValue) {
            return {
                color: "transparent",
                fillOpacity: 0,
            };
        }

        return {
            color: getColor(selectedValue, minValue, maxValue, highContrast, colorblind),
            fillOpacity: 0.7,
        };
    }

    function getCellPopupValue(cell: GridCell, filterKey: FilterKey) {
        return formatFilterValue(filterKey, cell[filterKey]);
    }

    return {
        selectedFilter,
        setSelectedFilter,
        minValue,
        setMinValue,
        maxValue,
        setMaxValue,
        language,
        setLanguage,
        darkMode,
        setDarkMode,
        gridSize,
        setGridSize,
        combinedMode,
        setCombinedMode,
        selectedFilters,
        minMaxPerFilter,
        highContrast,
        setHighContrast,
        visuallyImpaired,
        setVisuallyImpaired,
        colorblind,
        setColorblind,
        panelTitleColor,
        panelTextColor,
        panelMutedColor,
        baseFontSize,
        titleFontSize,
        subTitleFontSize,
        gridCells,
        formattedMinValue,
        formattedMaxValue,
        toggleCombinedFilter,
        updateCombinedFilterRange,
        getCellStyle,
        getCellPopupValue,
    };
}
