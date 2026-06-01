export type FilterKey = "AIR_QUALITY" | "CRIME" | "NOISE" | "PRICE";

export interface FilterDefinition {
    key: FilterKey;
    label: string;
    min: number;
    max: number;
    dataProvider?: string;
}

export const filterLabels: Record<FilterKey, string> = {
    AIR_QUALITY: "Jakość powietrza",
    CRIME: "Poziom przestępczości",
    NOISE: "Poziom hałasu",
    PRICE: "Cena za m²",
};

export function getFilterLabel(key: FilterKey) {
    return filterLabels[key] ?? key;
}

export function getFilterUnit(key: FilterKey) {
    if (key === "PRICE") return "PLN";
    if (key === "CRIME") return "%";
    if (key === "NOISE") return "dB";
    return "AQI";
}

export function formatFilterValue(key: FilterKey, value: number) {
    const unit = getFilterUnit(key);
    if (key === "AIR_QUALITY") return `${Math.round(value)} ${unit}`;
    if (key === "PRICE") return `${Math.round(value).toLocaleString("pl-PL")} ${unit}`;
    if (key === "CRIME") return `${Math.round(value)}${unit}`;
    if (key === "NOISE") return `${Math.round(value)} ${unit}`;
    return String(Math.round(value));
}

export function isFilterKey(value: string): value is FilterKey {
    return value in filterLabels;
}
