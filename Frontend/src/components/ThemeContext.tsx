import { createContext, useContext, useMemo, type ReactNode } from "react";

interface ThemeContextValue {
    readonly darkMode: boolean;
    readonly visuallyImpaired: boolean;
    readonly panelTitleColor: string;
    readonly panelTextColor: string;
    readonly panelMutedColor: string;
    readonly panelBorderColor: string;
    readonly sidebarBackground: string;
    readonly titleFontSize: string;
    readonly baseFontSize: string;
    readonly visuallyImpairedFontSize: string;
}

interface ThemeProviderProps {
    readonly darkMode: boolean;
    readonly visuallyImpaired: boolean;
    readonly children: ReactNode;
}

const ThemeContext = createContext<ThemeContextValue | null>(null);

export function ThemeProvider({ darkMode, visuallyImpaired, children }: ThemeProviderProps) {
    const panelTitleColor = darkMode ? "#f8f9fa" : "#495057";
    const panelTextColor = darkMode ? "#f8f9fa" : "#343a40";
    const panelMutedColor = darkMode ? "#ced4da" : "#6c757d";

    const value = useMemo<ThemeContextValue>(
        () => ({
            darkMode,
            visuallyImpaired,
            panelTitleColor,
            panelTextColor,
            panelMutedColor,
            panelBorderColor: darkMode ? "#495057" : "#ced4da",
            sidebarBackground: darkMode
                ? "linear-gradient(135deg, #2c2f33 0%, #23272a 100%)"
                : "linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%)",
            titleFontSize: visuallyImpaired ? "32px" : "22px",
            baseFontSize: visuallyImpaired ? "20px" : "14px",
            visuallyImpairedFontSize: visuallyImpaired ? "26px" : "16px",
        }),
        [darkMode, visuallyImpaired, panelMutedColor, panelTextColor, panelTitleColor],
    );

    return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>;
}

export function useTheme() {
    const theme = useContext(ThemeContext);

    if (!theme) {
        throw new Error("useTheme must be used within a ThemeProvider");
    }

    return theme;
}