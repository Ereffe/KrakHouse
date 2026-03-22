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

export default function Map() {
  return (
    <MapContainer center={KRAKOW} zoom={13} scrollWheelZoom>
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />
      <Polygon
        positions={KRAKOW_BORDER}
        pathOptions={{ color: "#d81b60", weight: 3, fillOpacity: 0.08 }}
      />
      <Marker position={KRAKOW}>
        <Popup>Krakow, Poland</Popup>
      </Marker>
    </MapContainer>
  );
}