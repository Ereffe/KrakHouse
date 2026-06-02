import type { FilterDefinition, FilterKey } from "../components/mapFilters";
import { getFilterLabel } from "../components/mapFilters";

const API_BASE_URL = "/api";

export interface MapBoundsDto {
    latitudeLeftBorder: number;
    latitudeRightBorder: number;
    longitudeTopBorder: number;
    longitudeBottomBorder: number;
}

const STATIC_MAP_BOUNDS: MapBoundsDto = {
     latitudeLeftBorder: 50.02,
    latitudeRightBorder: 50.12,
    longitudeTopBorder: 19.86,
    longitudeBottomBorder: 20.02,
};

export interface BackendFilterDto {
    type: FilterKey;
    min: number;
    max: number;
}

export interface FilteredMapRequestDto {
    mapFilter: FilterKey;
    minValue: number;
    maxValue: number;
}

export interface FilteredMapResponseDto {
    type: FilterKey;
    valueType: string;
    dataProvider: string;
    data: (number | null)[][];
}

export interface FilteredMapListResponseDto {
    bounds?: MapBoundsDto;
    maps: FilteredMapResponseDto[];
}

interface RawFilteredMapResponseDto {
    type: FilterKey;
    valueType: string;
    dataProvider: string;
    data: (number | null)[][];
}

interface RawFilteredMapListResponseDto {
    maps: RawFilteredMapResponseDto[];
}

export interface MergedMapResponseDto {
    bounds?: MapBoundsDto;
    type: Array<{
        type: FilterKey;
        dataProvider: string | null;
        lowerBound: number;
        upperBound: number;
    }>;
    data: boolean[][];
}

interface RawMergedMapResponseDto {
    type: Array<{
        type: FilterKey;
        dataProvider: string | null;
        lowerBound: number;
        upperBound: number;
    }>;
    data: boolean[][];
}

async function requestJson<T>(path: string, init?: RequestInit): Promise<T> {
    const headers = new Headers(init?.headers);
    headers.set("Content-Type", "application/json");

    const response = await fetch(`${API_BASE_URL}${path}`, {
        headers,
        ...init,
    });

    const rawBody = await response.text();
    const body = rawBody ? JSON.parse(rawBody) : null;

    if (!response.ok) {
        const message = typeof body === "object" && body && "message" in body ? String(body.message) : `Request failed with status ${response.status}`;
        throw new Error(message);
    }

    return body as T;
}

export async function fetchFilters(): Promise<BackendFilterDto[]> {
    return requestJson<BackendFilterDto[]>("/filters", { method: "GET" });
}

export async function fetchFilteredMaps(filters: FilteredMapRequestDto[]): Promise<FilteredMapListResponseDto> {
    const response = await requestJson<RawFilteredMapListResponseDto>("/maps-list", {
        method: "POST",
        body: JSON.stringify(filters),
    });

    return {
        bounds: STATIC_MAP_BOUNDS,
        maps: response.maps,
    };
}

export async function fetchMergedMaps(filters: FilteredMapRequestDto[]): Promise<MergedMapResponseDto> {
    const response = await requestJson<RawMergedMapResponseDto>("/maps", {
        method: "POST",
        body: JSON.stringify(filters),
    });

    return {
        bounds: STATIC_MAP_BOUNDS,
        type: response.type,
        data: response.data,
    };
}

export function toFilterDefinitions(filters: BackendFilterDto[]): FilterDefinition[] {
    return filters.map((filter) => ({
        key: filter.type,
        label: getFilterLabel(filter.type),
        min: filter.min,
        max: filter.max,
    }));
}