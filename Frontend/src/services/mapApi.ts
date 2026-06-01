import type { FilterDefinition, FilterKey } from "../components/mapFilters";
import { getFilterLabel } from "../components/mapFilters";

const API_BASE_URL = "/api";

export interface MapBoundsDto {
    latitudeLeftBorder: number;
    latitudeRightBorder: number;
    longitudeTopBorder: number;
    longitudeBottomBorder: number;
}

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
    return requestJson<BackendFilterDto[]>("/filters", { method: "POST" });
}

export async function fetchFilteredMaps(filters: FilteredMapRequestDto[]): Promise<FilteredMapListResponseDto> {
    return requestJson<FilteredMapListResponseDto>("/maps-list", {
        method: "POST",
        body: JSON.stringify(filters),
    });
}

export async function fetchMergedMaps(filters: FilteredMapRequestDto[]): Promise<MergedMapResponseDto> {
    return requestJson<MergedMapResponseDto>("/maps", {
        method: "POST",
        body: JSON.stringify(filters),
    });
}

export function toFilterDefinitions(filters: BackendFilterDto[]): FilterDefinition[] {
    return filters.map((filter) => ({
        key: filter.type,
        label: getFilterLabel(filter.type),
        min: filter.min,
        max: filter.max,
    }));
}