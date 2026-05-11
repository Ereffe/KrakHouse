export type FilterKey = "lifeScore" | "price" | "pollution" | "noise" | "crimeLevel";

export interface FilterDefinition {
    key: FilterKey;
    label: string;
    min: number;
    max: number;
}

export const filters: FilterDefinition[] = [
    { key: "lifeScore", label: "Life Score", min: 0, max: 100 },
    { key: "price", label: "Cena/m^2", min: 10, max: 500 },
    {
        key: "pollution",
        label: "Zanieczyszczenie powietrza",
        min: 1,
        max: 10,
    },
    { key: "noise", label: "Poziom hałasu", min: 0, max: 10 },
    {
        key: "crimeLevel",
        label: "Poziom przestępczości",
        min: 0,
        max: 10,
    },
];
