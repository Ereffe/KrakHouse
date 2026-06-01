import { useEffect, useMemo, useState } from "react";
import {
    fetchFilteredMaps,
    fetchFilters,
    fetchMergedMaps,
    type FilteredMapListResponseDto,
    type FilteredMapRequestDto,
    type MergedMapResponseDto,
} from "../services/mapApi";
import {
    formatFilterValue,
    getFilterLabel,
    type FilterDefinition,
    type FilterKey,
} from "./mapFilters";

export interface GridCell {
    id: string;
    positions: [number, number][];
    values: Partial<Record<FilterKey, number>>;
    visible: boolean;
}

type MinMax = { min: number; max: number };
type MinMaxPerFilter = Record<FilterKey, MinMax>;

export const KRAKOW: [number, number] = [50.0647, 19.945];
const DEFAULT_MAP_BOUNDS = {
    latitudeLeftBorder: 50.02,
    latitudeRightBorder: 50.12,
    longitudeTopBorder: 19.86,
    longitudeBottomBorder: 20.02,
};

const DEFAULT_MIN_MAX: MinMaxPerFilter = {
    AIR_QUALITY: { min: 0, max: 500 },
    CRIME: { min: 0, max: 100 },
    NOISE: { min: 30, max: 90 },
    PRICE: { min: 1000, max: 10000 },
};

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
        const normalized = (value - min) / (max - min || 1);
        return normalized < 0.5 ? "#000000" : "#FFFFFF";
    }

    if (colorblind) {
        if (value <= min) return "#1f77b4";
        if (value >= max) return "#ff7f0e";
        const normalized = (value - min) / (max - min || 1);
        const r = Math.floor(255 * normalized * 0.8 + 31);
        const g = Math.floor(119 * (1 - normalized) + 127 * normalized);
        const b = Math.floor(180 * (1 - normalized) + 14 * normalized);
        return `rgb(${r}, ${g}, ${b})`;
    }

    if (value <= min) return "#dc3545";
    if (value >= max) return "#28a745";

    const normalized = (value - min) / (max - min || 1);
    const r = Math.floor(255 * (1 - normalized));
    const g = Math.floor(255 * normalized);
    return `rgb(${r}, ${g}, 0)`;
}

function buildRequestFilters(
    combinedMode: boolean,
    selectedFilter: FilterKey,
    minValue: number,
    maxValue: number,
    selectedFilters: FilterKey[],
    minMaxPerFilter: MinMaxPerFilter,
): FilteredMapRequestDto[] {
    if (combinedMode) {
        return selectedFilters.map((filterKey) => ({
            mapFilter: filterKey,
            minValue: minMaxPerFilter[filterKey].min,
            maxValue: minMaxPerFilter[filterKey].max,
        }));
    }

    return [
        {
            mapFilter: selectedFilter,
            minValue,
            maxValue,
        },
    ];
}

function buildPolygonPositions(
    bounds: NonNullable<FilteredMapListResponseDto["bounds"]>,
    rowIndex: number,
    columnIndex: number,
    rowCount: number,
    columnCount: number,
): [number, number][] {
    const latStep = (bounds.latitudeRightBorder - bounds.latitudeLeftBorder) / rowCount;
    const lonStep = (bounds.longitudeBottomBorder - bounds.longitudeTopBorder) / columnCount;

    const lat1 = bounds.latitudeLeftBorder + rowIndex * latStep;
    const lat2 = bounds.latitudeLeftBorder + (rowIndex + 1) * latStep;
    const lon1 = bounds.longitudeTopBorder + columnIndex * lonStep;
    const lon2 = bounds.longitudeTopBorder + (columnIndex + 1) * lonStep;

    return [
        [lat1, lon1],
        [lat2, lon1],
        [lat2, lon2],
        [lat1, lon2],
    ];
}

function isInRange(value: number | null | undefined, min: number, max: number) {
    return typeof value === "number" && value >= min && value <= max;
}

function buildGridCells({
    listResponse,
    mergedResponse,
    combinedMode,
    selectedFilter,
    selectedFilters,
    minMaxPerFilter,
}: {
    listResponse: FilteredMapListResponseDto | null;
    mergedResponse: MergedMapResponseDto | null;
    combinedMode: boolean;
    selectedFilter: FilterKey;
    selectedFilters: FilterKey[];
    minMaxPerFilter: MinMaxPerFilter;
}): GridCell[] {
    if (!listResponse || listResponse.maps.length === 0) {
        return [];
    }

    const baseMap =
        listResponse.maps.find((map) => map.type === selectedFilter) ?? listResponse.maps[0];
    const rowCount = baseMap.data.length;
    const columnCount = baseMap.data[0]?.length ?? 0;
    const bounds = listResponse.bounds ?? mergedResponse?.bounds ?? DEFAULT_MAP_BOUNDS;

    if (!rowCount || !columnCount) {
        return [];
    }

    const mapByType = new Map(
        listResponse.maps.map((map) => [map.type, map.data] as const),
    );

    return baseMap.data.flatMap((row, rowIndex) =>
        row.map((_, columnIndex) => {
            const values: Partial<Record<FilterKey, number>> = {};

            const sourceFilters = combinedMode ? selectedFilters : [selectedFilter];
            for (const filterKey of sourceFilters) {
                const matrix = mapByType.get(filterKey);
                const sourceValue = matrix?.[rowIndex]?.[columnIndex];
                if (typeof sourceValue === "number") {
                    values[filterKey] = sourceValue;
                }
            }

            const combinedVisible =
                !combinedMode ||
                (mergedResponse?.data[rowIndex]?.[columnIndex] ?? false);

            const singleVisible = isInRange(
                values[selectedFilter],
                minMaxPerFilter[selectedFilter].min,
                minMaxPerFilter[selectedFilter].max,
            );

            const visible = combinedMode ? combinedVisible : singleVisible;

            return {
                id: `${rowIndex}-${columnIndex}`,
                positions: buildPolygonPositions(
                    bounds,
                    rowIndex,
                    columnIndex,
                    rowCount,
                    columnCount,
                ),
                values,
                visible,
            } satisfies GridCell;
        }),
    );
}

function getSelectedFilterConfig(filters: FilterDefinition[], selectedFilter: FilterKey) {
    return filters.find((filter) => filter.key === selectedFilter) ?? filters[0];
}

function hasFilterKey(filters: FilterDefinition[], filterKey: FilterKey) {
    for (const filter of filters) {
        if (filter.key === filterKey) {
            return true;
        }
    }

    return false;
}

function clamp(value: number, min: number, max: number) {
    return Math.min(Math.max(value, min), max);
}

export function useMapController() {
    const [filters, setFilters] = useState<FilterDefinition[]>([]);
    const [selectedFilter, setSelectedFilter] = useState<FilterKey>("AIR_QUALITY");
    const [minValue, setMinValue] = useState(0);
    const [maxValue, setMaxValue] = useState(10);
    const [language, setLanguage] = useState<"pl" | "en">("pl");
    const [darkMode, setDarkMode] = useState(false);
    const [gridSize, setGridSize] = useState(13);
    const [combinedMode, setCombinedMode] = useState(false);
    const [selectedFilters, setSelectedFilters] = useState<FilterKey[]>([]);
    const [minMaxPerFilter, setMinMaxPerFilter] = useState<MinMaxPerFilter>(DEFAULT_MIN_MAX);
    const [highContrast, setHighContrast] = useState(false);
    const [visuallyImpaired, setVisuallyImpaired] = useState(false);
    const [colorblind, setColorblind] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [listResponse, setListResponse] = useState<FilteredMapListResponseDto | null>(null);
    const [mergedResponse, setMergedResponse] = useState<MergedMapResponseDto | null>(null);

    const panelTitleColor = darkMode ? "#f8f9fa" : "#495057";
    const panelTextColor = darkMode ? "#f8f9fa" : "#343a40";
    const panelMutedColor = darkMode ? "#ced4da" : "#6c757d";
    const baseFontSize = visuallyImpaired ? "20px" : "14px";
    const titleFontSize = visuallyImpaired ? "32px" : "22px";
    const subTitleFontSize = visuallyImpaired ? "24px" : "18px";

    useEffect(() => {
        let cancelled = false;

        setIsLoading(true);
        fetchFilters()
            .then((backendFilters) => {
                if (cancelled) return;

                const nextFilters = backendFilters.map((filter) => ({
                    key: filter.type,
                    label: getFilterLabel(filter.type),
                    min: filter.min,
                    max: filter.max,
                }));

                setFilters(nextFilters);
                // keep frontend default ranges for all filters
                setMinMaxPerFilter(DEFAULT_MIN_MAX);

                const firstFilter = nextFilters[0];
                if (firstFilter) {
                    const selectedKey = hasFilterKey(nextFilters, selectedFilter)
                        ? selectedFilter
                        : firstFilter.key;
                    setSelectedFilter(() => selectedKey);

                    // initialize slider values from our frontend-default ranges
                    const def = DEFAULT_MIN_MAX[selectedKey];
                    if (def) {
                        setMinValue(def.min);
                        setMaxValue(def.max);
                    } else {
                        setMinValue(firstFilter.min);
                        setMaxValue(firstFilter.max);
                    }
                }

                setError(null);
                setIsLoading(false);
            })
            .catch((fetchError: unknown) => {
                if (cancelled) return;
                setError(fetchError instanceof Error ? fetchError.message : "Failed to load filters");
                setIsLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, []);

    useEffect(() => {
        const currentFilter = getSelectedFilterConfig(filters, selectedFilter);
        const mm = minMaxPerFilter[selectedFilter];
        if (mm) {
            setMinValue(mm.min);
            setMaxValue(mm.max);
            return;
        }

        if (!currentFilter) return;

        setMinValue(currentFilter.min);
        setMaxValue(currentFilter.max);
    }, [filters, selectedFilter, minMaxPerFilter]);

    useEffect(() => {
        if (!combinedMode && selectedFilters.length > 0) {
            return;
        }

        if (combinedMode && selectedFilters.length === 0 && filters.length > 0) {
            setSelectedFilters([selectedFilter]);
        }
    }, [combinedMode, filters.length, selectedFilter, selectedFilters.length]);

    useEffect(() => {
        if (filters.length === 0) {
            return;
        }

        const requestFilters = buildRequestFilters(
            combinedMode,
            selectedFilter,
            minValue,
            maxValue,
            selectedFilters,
            minMaxPerFilter,
        );

        if (combinedMode && requestFilters.length === 0) {
            setListResponse(null);
            setMergedResponse(null);
            setIsLoading(false);
            setError(null);
            return;
        }

        let cancelled = false;

        setIsLoading(true);
        setError(null);

        Promise.all([
            fetchFilteredMaps(requestFilters),
            combinedMode ? fetchMergedMaps(requestFilters) : Promise.resolve(null),
        ])
            .then(([nextListResponse, nextMergedResponse]) => {
                if (cancelled) return;
                setListResponse(nextListResponse);
                setMergedResponse(nextMergedResponse);

                // no server-derived range adjustments; frontend uses initial `minMaxPerFilter`
                setIsLoading(false);
            })
            .catch((fetchError: unknown) => {
                if (cancelled) return;
                setListResponse(null);
                setMergedResponse(null);
                setError(fetchError instanceof Error ? fetchError.message : "Failed to load map data");
                setIsLoading(false);
            });

        return () => {
            cancelled = true;
        };
    }, [combinedMode, filters.length, maxValue, minMaxPerFilter, minValue, selectedFilter, selectedFilters]);

    const formattedMinValue = formatFilterValue(selectedFilter, minValue);
    const formattedMaxValue = formatFilterValue(selectedFilter, maxValue);
    const resolvedBounds = listResponse?.bounds ?? mergedResponse?.bounds ?? DEFAULT_MAP_BOUNDS;
    const mapCenter: [number, number] = listResponse?.bounds ?? mergedResponse?.bounds
        ? [
            (resolvedBounds.latitudeLeftBorder + resolvedBounds.latitudeRightBorder) / 2,
            (resolvedBounds.longitudeTopBorder + resolvedBounds.longitudeBottomBorder) / 2,
        ]
        : KRAKOW;

    const gridCells = useMemo(
        () =>
            buildGridCells({
                listResponse,
                mergedResponse,
                combinedMode,
                selectedFilter,
                selectedFilters,
                minMaxPerFilter,
            }),
        [combinedMode, listResponse, mergedResponse, minMaxPerFilter, selectedFilter, selectedFilters],
    );

    function toggleCombinedFilter(filterKey: FilterKey, checked: boolean) {
        if (checked) {
            setSelectedFilters((prev) => (prev.includes(filterKey) ? prev : [...prev, filterKey]));
            return;
        }

        setSelectedFilters((prev) => prev.filter((key) => key !== filterKey));
    }

    function updateCombinedFilterRange(
        filterKey: FilterKey,
        rangeType: "min" | "max",
        value: number,
    ) {
        setMinMaxPerFilter((prev) => {
            const currentRange = prev[filterKey] ?? DEFAULT_MIN_MAX[filterKey];
            const bounds =
                filters.find((filter) => filter.key === filterKey) ??
                ({ min: currentRange.min, max: currentRange.max } as const);

            if (rangeType === "min") {
                const nextMin = clamp(value, bounds.min, currentRange.max);
                return {
                    ...prev,
                    [filterKey]: {
                        min: nextMin,
                        max: currentRange.max,
                    },
                };
            }

            const nextMax = clamp(value, currentRange.min, bounds.max);
            return {
                ...prev,
                [filterKey]: {
                    min: currentRange.min,
                    max: nextMax,
                },
            };
        });
    }

    function getCombinedModeColor() {
        if (highContrast) return "#FFFFFF";
        if (colorblind) return "#ff7f0e";
        return "#28a745";
    }

    function getCellStyle(cell: GridCell): { color: string; fillOpacity: number } | null {
        if (!cell.visible) {
            return null;
        }

        if (combinedMode) {
            return {
                color: getCombinedModeColor(),
                fillOpacity: 0.7,
            };
        }

        const selectedValue = cell.values[selectedFilter];
        if (selectedValue == null) {
            return null;
        }

        const { min, max } = minMaxPerFilter[selectedFilter];

        return {
            color: getColor(selectedValue, min, max, highContrast, colorblind),
            fillOpacity: 0.7,
        };
    }

    function getCellPopupValue(cell: GridCell, filterKey: FilterKey) {
        const value = cell.values[filterKey];
        if (value == null) {
            return "Brak danych";
        }

        return formatFilterValue(filterKey, value);
    }

    return {
        filters,
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
        mapCenter,
        toggleCombinedFilter,
        updateCombinedFilterRange,
        getCellStyle,
        getCellPopupValue,
        isLoading,
        error,
        selectedFilterConfig: minMaxPerFilter[selectedFilter] ?? getSelectedFilterConfig(filters, selectedFilter),
        mapBounds: resolvedBounds,
        requestCount: listResponse?.maps.length ?? 0,
        getFilterLabel,
    };
}