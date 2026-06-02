import type { Language } from "./i18n";

export type FilterKey = "AIR_QUALITY" | "CRIME" | "NOISE" | "PRICE";

export interface FilterDefinition {
    key: FilterKey;
    label: string;
    min: number;
    max: number;
    dataProvider?: string;
}

export const PRICE_FILTER_RANGE = { min: 5000, max: 60000 };

export function getFrontendFilterRange(filter: Pick<FilterDefinition, "key" | "min" | "max">) {
    if (filter.key === "PRICE") {
        return PRICE_FILTER_RANGE;
    }

    return {
        min: filter.min,
        max: filter.max,
    };
}

export const filterLabels: Record<Language, Record<FilterKey, string>> = {
    pl: {
        AIR_QUALITY: "Jakosc powietrza",
        CRIME: "Poziom przestepczosci",
        NOISE: "Poziom halasu",
        PRICE: "Cena za m2",
    },
    en: {
        AIR_QUALITY: "Air quality",
        CRIME: "Crime level",
        NOISE: "Noise level",
        PRICE: "Price per m2",
    },
};

export function getFilterLabel(key: FilterKey, language: Language = "pl") {
    return filterLabels[language][key] ?? key;
}

export function getFilterUnit(key: FilterKey) {
    if (key === "PRICE") return "PLN";
    if (key === "CRIME") return "%";
    if (key === "NOISE") return "dB";
    return "AQI";
}

export function formatFilterValue(key: FilterKey, value: number, language: Language = "pl") {
    const unit = getFilterUnit(key);
    if (key === "AIR_QUALITY") return `${Math.round(value)} ${unit}`;
    if (key === "PRICE") return `${Math.round(value).toLocaleString(language === "pl" ? "pl-PL" : "en-US")} ${unit}`;
    if (key === "CRIME") return `${Math.round(value)}${unit}`;
    if (key === "NOISE") return `${Math.round(value)} ${unit}`;
    return String(Math.round(value));
}

export function isFilterKey(value: string): value is FilterKey {
    return value in filterLabels.pl;
}
