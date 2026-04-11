import { useMemo, useState, useEffect } from "react";
import { MapContainer, Marker, Polygon, Popup, TileLayer } from "react-leaflet";

const KRAKOW: [number, number] = [50.0647, 19.945];
const KRAKOW_BORDER: [number, number][] = [
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

interface GridCell {
  id: string;
  positions: [number, number][];
  lifeScore: number;
  price: number;
  pollution: number;
  noise: number;
  crimeLevel: number;
}

// Generate grid cells
const minLat = 49.948;
const maxLat = 50.165;
const minLon = 19.776;
const maxLon = 20.107;
const initialGridSize = 10;

const filters = [
  { key: "lifeScore" as const, label: "Life Score", min: 0, max: 100 },
  { key: "price" as const, label: "Cena/m^2", min: 10, max: 500 },
  {
    key: "pollution" as const,
    label: "Zanieczyszczenie powietrza",
    min: 1,
    max: 10,
  },
  { key: "noise" as const, label: "Poziom hałasu", min: 0, max: 10 },
  {
    key: "crimeLevel" as const,
    label: "Poziom przestępczości",
    min: 0,
    max: 10,
  },
];

function getColor(
  value: number,
  min: number,
  max: number,
  highContrast: boolean,
  colorblind: boolean,
) {
  if (highContrast) {
    if (value <= min) return "#000000"; // Black for low
    if (value >= max) return "#FFFFFF"; // White for high
    const normalized = (value - min) / (max - min);
    return normalized < 0.5 ? "#000000" : "#FFFFFF"; // Black or white
  }
  if (colorblind) {
    // Colorblind-friendly palette: blue to orange
    if (value <= min) return "#1f77b4"; // Blue
    if (value >= max) return "#ff7f0e"; // Orange
    const normalized = (value - min) / (max - min);
    const r = Math.floor(255 * normalized * 0.8 + 31);
    const g = Math.floor(119 * (1 - normalized) + 127 * normalized);
    const b = Math.floor(180 * (1 - normalized) + 14 * normalized);
    return `rgb(${r}, ${g}, ${b})`;
  }
  // Default
  if (value <= min) return "#dc3545";
  if (value >= max) return "#28a745";
  const normalized = (value - min) / (max - min);
  const r = Math.floor(255 * (1 - normalized));
  const g = Math.floor(255 * normalized);
  const b = 0;
  return `rgb(${r}, ${g}, ${b})`;
}

export default function Map() {
  const [selectedFilter, setSelectedFilter] = useState<
    "lifeScore" | "price" | "pollution" | "noise" | "crimeLevel"
  >("lifeScore");
  const [minValue, setMinValue] = useState(0);
  const [maxValue, setMaxValue] = useState(10);
  const [language, setLanguage] = useState<"pl" | "en">("pl");
  const [darkMode, setDarkMode] = useState(false);
  const [gridSize, setGridSize] = useState(initialGridSize);
  const [combinedMode, setCombinedMode] = useState(false);
  const [selectedFilters, setSelectedFilters] = useState<string[]>([]);
  const [minMaxPerFilter, setMinMaxPerFilter] = useState<
    Record<string, { min: number; max: number }>
  >({
    lifeScore: { min: 0, max: 100 },
    price: { min: 10, max: 500 },
    pollution: { min: 1, max: 10 },
    noise: { min: 0, max: 10 },
    crimeLevel: { min: 0, max: 10 },
  });
  const [highContrast, setHighContrast] = useState(false);
  const [visuallyImpaired, setVisuallyImpaired] = useState(false);
  const [colorblind, setColorblind] = useState(false);

  const panelTitleColor = darkMode ? "#f8f9fa" : "#495057";
  const panelTextColor = darkMode ? "#f8f9fa" : "#343a40";
  const panelMutedColor = darkMode ? "#ced4da" : "#6c757d";
  const baseFontSize = visuallyImpaired ? "20px" : "14px";
  const titleFontSize = visuallyImpaired ? "32px" : "22px";
  const subTitleFontSize = visuallyImpaired ? "24px" : "18px";

  const gridCells = useMemo(() => {
    const cells: GridCell[] = [];
    const rows = gridSize;
    const cols = gridSize;
    const latStep = (maxLat - minLat) / rows;
    const lonStep = (maxLon - minLon) / cols;
    for (let i = 0; i < rows; i++) {
      for (let j = 0; j < cols; j++) {
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
  }, [gridSize]);

  useEffect(() => {
    const filter = filters.find((f) => f.key === selectedFilter);
    if (filter) {
      setMinValue(filter.min);
      setMaxValue(filter.max);
    }
  }, [selectedFilter]);

  return (
    <div
      style={{
        display: "flex",
        height: "100vh",
        fontFamily: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif",
      }}
    >
      <div
        style={{
          width: "280px",
          padding: "25px",
          background: darkMode
            ? "linear-gradient(135deg, #2c2f33 0%, #23272a 100%)"
            : "linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%)",
          borderRight: "2px solid #dee2e6",
          overflowY: "auto",
          boxShadow: "2px 0 10px rgba(0, 0, 0, 0.1)",
          borderRadius: "0 15px 15px 0",
          color: darkMode ? "#f8f9fa" : "#343a40",
        }}
      >
        <h3
          style={{
            marginTop: 0,
            marginBottom: "25px",
            fontSize: titleFontSize,
            color: panelTitleColor,
            fontWeight: "600",
            textAlign: "center",
            borderBottom: "2px solid #6c757d",
            paddingBottom: "10px",
          }}
        >
          Filtry
        </h3>
        <div style={{ marginBottom: "20px" }}>
          <label
            style={{
              display: "flex",
              alignItems: "center",
              cursor: "pointer",
              fontSize: baseFontSize,
              color: panelTextColor,
              fontWeight: "500",
            }}
          >
            <input
              type="checkbox"
              checked={combinedMode}
              onChange={(e) => setCombinedMode(e.target.checked)}
              style={{
                marginRight: "10px",
                width: "18px",
                height: "18px",
                accentColor: "#28a745",
              }}
            />
            Tryb połączonych filtrów
          </label>
        </div>
        {combinedMode ? (
          <>
            <div style={{ marginBottom: "25px" }}>
              <label
                style={{
                  display: "block",
                  marginBottom: "15px",
                  fontWeight: "500",
                  color: panelTextColor,
                  fontSize: baseFontSize,
                }}
              >
                Wybierz filtry:
              </label>
              {filters.map((filter) => (
                <div key={filter.key} style={{ marginBottom: "10px" }}>
                  <label
                    style={{
                      display: "flex",
                      alignItems: "center",
                      cursor: "pointer",
                      fontSize: "14px",
                      color: selectedFilters.includes(filter.key)
                        ? "#28a745"
                        : panelTextColor,
                      fontWeight: selectedFilters.includes(filter.key)
                        ? "700"
                        : "500",
                    }}
                  >
                    <input
                      type="checkbox"
                      checked={selectedFilters.includes(filter.key)}
                      onChange={(e) => {
                        if (e.target.checked) {
                          setSelectedFilters([...selectedFilters, filter.key]);
                        } else {
                          setSelectedFilters(
                            selectedFilters.filter((f) => f !== filter.key),
                          );
                        }
                      }}
                      style={{
                        marginRight: "10px",
                      }}
                    />
                    {filter.label}
                  </label>
                  {selectedFilters.includes(filter.key) && (
                    <div style={{ marginLeft: "25px", marginTop: "10px" }}>
                      <div style={{ marginBottom: "10px" }}>
                        <label
                          style={{
                            display: "block",
                            marginBottom: "5px",
                            fontSize: "12px",
                            color: panelTextColor,
                          }}
                        >
                          Min: {minMaxPerFilter[filter.key].min}
                        </label>
                        <input
                          type="range"
                          min={filter.min}
                          max={filter.max}
                          step={filter.key === "lifeScore" ? 1 : 0.1}
                          value={minMaxPerFilter[filter.key].min}
                          onChange={(e) =>
                            setMinMaxPerFilter({
                              ...minMaxPerFilter,
                              [filter.key]: {
                                ...minMaxPerFilter[filter.key],
                                min: Number(e.target.value),
                              },
                            })
                          }
                          style={{
                            width: "100%",
                            height: "6px",
                            borderRadius: "5px",
                            background:
                              "linear-gradient(to right, #dc3545, #28a745)",
                            outline: "none",
                            cursor: "pointer",
                          }}
                        />
                      </div>
                      <div>
                        <label
                          style={{
                            display: "block",
                            marginBottom: "5px",
                            fontSize: "12px",
                            color: panelTextColor,
                          }}
                        >
                          Max: {minMaxPerFilter[filter.key].max}
                        </label>
                        <input
                          type="range"
                          min={filter.min}
                          max={filter.max}
                          step={filter.key === "lifeScore" ? 1 : 0.1}
                          value={minMaxPerFilter[filter.key].max}
                          onChange={(e) =>
                            setMinMaxPerFilter({
                              ...minMaxPerFilter,
                              [filter.key]: {
                                ...minMaxPerFilter[filter.key],
                                max: Number(e.target.value),
                              },
                            })
                          }
                          style={{
                            width: "100%",
                            height: "6px",
                            borderRadius: "5px",
                            background:
                              "linear-gradient(to right, #dc3545, #28a745)",
                            outline: "none",
                            cursor: "pointer",
                          }}
                        />
                      </div>
                    </div>
                  )}
                </div>
              ))}
            </div>
            {selectedFilters.length > 0 && (
              <div style={{ marginBottom: "25px" }}>
                <h4
                  style={{
                    color: panelTitleColor,
                    fontSize: "16px",
                    marginBottom: "10px",
                  }}
                >
                  Wybrane filtry:
                </h4>
                {selectedFilters.map((f) => (
                  <div
                    key={f}
                    style={{
                      color: "#28a745",
                      fontWeight: "bold",
                      fontSize: "14px",
                    }}
                  >
                    {filters.find((fl) => fl.key === f)?.label}
                  </div>
                ))}
              </div>
            )}
          </>
        ) : (
          <>
            <div style={{ marginBottom: "25px" }}>
              <label
                style={{
                  display: "block",
                  marginBottom: "15px",
                  fontWeight: "500",
                  color: panelTextColor,
                  fontSize: baseFontSize,
                }}
              >
                Wybierz filtr:
              </label>
              {filters.map((filter) => (
                <div key={filter.key} style={{ marginBottom: "10px" }}>
                  <label
                    style={{
                      display: "flex",
                      alignItems: "center",
                      cursor: "pointer",
                      fontSize: "14px",
                      color:
                        selectedFilter === filter.key
                          ? "#28a745"
                          : panelTextColor,
                      fontWeight: selectedFilter === filter.key ? "700" : "500",
                    }}
                  >
                    <input
                      type="radio"
                      name="filter"
                      value={filter.key}
                      checked={selectedFilter === filter.key}
                      onChange={(e) =>
                        setSelectedFilter(
                          e.target.value as typeof selectedFilter,
                        )
                      }
                      style={{
                        marginRight: "10px",
                      }}
                    />
                    {filter.label}
                  </label>
                </div>
              ))}
            </div>
            <div>
              <label
                style={{
                  display: "block",
                  marginBottom: "8px",
                  fontWeight: "500",
                  color: panelTextColor,
                  fontSize: baseFontSize,
                }}
              >
                Min:{" "}
                <span style={{ color: "#dc3545", fontWeight: "bold" }}>
                  {selectedFilter === "price"
                    ? `${minValue} PLN`
                    : selectedFilter === "lifeScore"
                      ? `${minValue}%`
                      : minValue}
                </span>
              </label>
              <input
                type="range"
                min={filters.find((f) => f.key === selectedFilter)?.min || 0}
                max={filters.find((f) => f.key === selectedFilter)?.max || 10}
                step={selectedFilter === "lifeScore" ? 1 : 0.1}
                value={minValue}
                onChange={(e) => setMinValue(Number(e.target.value))}
                style={{
                  width: "100%",
                  height: "8px",
                  borderRadius: "10px",
                  background:
                    "linear-gradient(to right, #dc3545, #ffc107, #28a745)",
                  outline: "none",
                  cursor: "pointer",
                  appearance: "none",
                  WebkitAppearance: "none",
                }}
              />
            </div>
            <div style={{ marginTop: "15px" }}>
              <label
                style={{
                  display: "block",
                  marginBottom: "8px",
                  fontWeight: "500",
                  color: panelTextColor,
                  fontSize: baseFontSize,
                }}
              >
                Max:{" "}
                <span style={{ color: "#28a745", fontWeight: "bold" }}>
                  {selectedFilter === "price"
                    ? `${maxValue} PLN`
                    : selectedFilter === "lifeScore"
                      ? `${maxValue}%`
                      : maxValue}
                </span>
              </label>
              <input
                type="range"
                min={filters.find((f) => f.key === selectedFilter)?.min || 0}
                max={filters.find((f) => f.key === selectedFilter)?.max || 10}
                step={selectedFilter === "lifeScore" ? 1 : 0.1}
                value={maxValue}
                onChange={(e) => setMaxValue(Number(e.target.value))}
                style={{
                  width: "100%",
                  height: "8px",
                  borderRadius: "10px",
                  background:
                    "linear-gradient(to right, #dc3545, #ffc107, #28a745)",
                  outline: "none",
                  cursor: "pointer",
                  appearance: "none",
                  WebkitAppearance: "none",
                }}
              />
            </div>
          </>
        )}
        <div
          style={{
            marginTop: "30px",
            paddingTop: "20px",
            borderTop: darkMode ? "1px solid #495057" : "1px solid #ced4da",
          }}
        >
          <h4
            style={{
              margin: 0,
              marginBottom: "16px",
              fontSize: subTitleFontSize,
              fontWeight: "600",
              color: darkMode ? "#f8f9fa" : "#495057",
            }}
          >
            Ustawienia
          </h4>
          <div style={{ marginBottom: "16px" }}>
            <label
              style={{
                display: "block",
                marginBottom: "8px",
                fontWeight: "500",
                color: panelTextColor,
              }}
            >
              Rozmiar siatki: {gridSize} x {gridSize}
            </label>
            <input
              type="range"
              min={5}
              max={20}
              step={1}
              value={gridSize}
              onChange={(e) => setGridSize(Number(e.target.value))}
              style={{
                width: "100%",
                height: "8px",
                borderRadius: "10px",
                background: "linear-gradient(to right, #6f42c1, #6610f2)",
                outline: "none",
                cursor: "pointer",
                appearance: "none",
                WebkitAppearance: "none",
              }}
            />
          </div>
          <div style={{ marginBottom: "16px" }}>
            <label
              style={{
                display: "block",
                marginBottom: "8px",
                fontWeight: "500",
                color: panelTextColor,
              }}
            >
              Język strony
            </label>
            <select
              value={language}
              onChange={(e) => setLanguage(e.target.value as "pl" | "en")}
              style={{
                width: "100%",
                padding: "10px 14px",
                borderRadius: "10px",
                border: "1px solid #ced4da",
                backgroundColor: darkMode ? "#343a40" : "#ffffff",
                color: darkMode ? "#f8f9fa" : "#495057",
                cursor: "pointer",
                fontSize: "14px",
              }}
            >
              <option value="pl">Polski</option>
              <option value="en">English</option>
            </select>
          </div>
          <div>
            <label
              style={{
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                fontWeight: "500",
                color: panelTextColor,
                marginBottom: "10px",
              }}
            >
              Tryb ciemny
              <input
                type="checkbox"
                checked={darkMode}
                onChange={(e) => setDarkMode(e.target.checked)}
                style={{
                  width: "18px",
                  height: "18px",
                  accentColor: "#28a745",
                  cursor: "pointer",
                }}
              />
            </label>
            <p
              style={{
                margin: 0,
                fontSize: "13px",
                color: panelMutedColor,
              }}
            >
              Przykładowe ustawienia interfejsu.
            </p>
          </div>
          <div style={{ marginTop: "20px" }}>
            <h5
              style={{
                margin: 0,
                marginBottom: "12px",
                fontSize: visuallyImpaired ? "26px" : "16px",
                fontWeight: "600",
                color: darkMode ? "#f8f9fa" : "#495057",
              }}
            >
              Dostępność
            </h5>
            <div style={{ marginBottom: "12px" }}>
              <label
                style={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                  fontWeight: "500",
                  color: panelTextColor,
                  fontSize: visuallyImpaired ? "20px" : "14px",
                }}
              >
                🔆 Wysoki kontrast
                <input
                  type="checkbox"
                  checked={highContrast}
                  onChange={(e) => setHighContrast(e.target.checked)}
                  style={{
                    width: visuallyImpaired ? "24px" : "18px",
                    height: visuallyImpaired ? "24px" : "18px",
                    accentColor: "#28a745",
                    cursor: "pointer",
                  }}
                />
              </label>
            </div>
            <div style={{ marginBottom: "12px" }}>
              <label
                style={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                  fontWeight: "500",
                  color: panelTextColor,
                  fontSize: visuallyImpaired ? "20px" : "14px",
                }}
              >
                👁️ Dla słabo widzących
                <input
                  type="checkbox"
                  checked={visuallyImpaired}
                  onChange={(e) => setVisuallyImpaired(e.target.checked)}
                  style={{
                    width: visuallyImpaired ? "24px" : "18px",
                    height: visuallyImpaired ? "24px" : "18px",
                    accentColor: "#28a745",
                    cursor: "pointer",
                  }}
                />
              </label>
            </div>
            <div>
              <label
                style={{
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "space-between",
                  fontWeight: "500",
                  color: panelTextColor,
                  fontSize: visuallyImpaired ? "20px" : "14px",
                }}
              >
                🌈 Dla daltonistów
                <input
                  type="checkbox"
                  checked={colorblind}
                  onChange={(e) => setColorblind(e.target.checked)}
                  style={{
                    width: visuallyImpaired ? "24px" : "18px",
                    height: visuallyImpaired ? "24px" : "18px",
                    accentColor: "#28a745",
                    cursor: "pointer",
                  }}
                />
              </label>
            </div>
          </div>
        </div>
      </div>
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
          {gridCells.map((cell) => {
            let color: string;
            let fillOpacity: number;
            let meetsAll = false;
            if (combinedMode) {
              meetsAll =
                selectedFilters.length > 0 &&
                selectedFilters.every((f) => {
                  const val = cell[f as keyof GridCell] as number;
                  const { min, max } = minMaxPerFilter[f];
                  return val >= min && val <= max;
                });
              if (!meetsAll) return null; // Don't render cells that don't meet requirements
              color = highContrast
                ? "#FFFFFF"
                : colorblind
                  ? "#ff7f0e"
                  : "#28a745";
              fillOpacity = 0.7;
            } else {
              const val = cell[selectedFilter];
              if (val < minValue || val > maxValue) {
                color = "transparent";
                fillOpacity = 0;
              } else {
                color = getColor(
                  val,
                  minValue,
                  maxValue,
                  highContrast,
                  colorblind,
                );
                fillOpacity = 0.7;
              }
            }
            return (
              <Polygon
                key={cell.id}
                positions={cell.positions}
                pathOptions={{
                  color,
                  weight: 1,
                  fillOpacity,
                }}
              >
                <Popup>
                  Cell {cell.id}
                  <br />
                  {combinedMode ? (
                    selectedFilters.map((f) => (
                      <div key={f}>
                        {filters.find((fl) => fl.key === f)?.label}:{" "}
                        {cell[f as keyof GridCell]}
                      </div>
                    ))
                  ) : (
                    <>
                      {filters.find((f) => f.key === selectedFilter)?.label}:{" "}
                      {selectedFilter === "price"
                        ? `${cell[selectedFilter]} PLN`
                        : selectedFilter === "lifeScore"
                          ? `${cell[selectedFilter]}%`
                          : cell[selectedFilter]}
                    </>
                  )}
                </Popup>
              </Polygon>
            );
          })}
          <Marker position={KRAKOW}>
            <Popup>Krakow, Poland</Popup>
          </Marker>
        </MapContainer>
      </div>
    </div>
  );
}
