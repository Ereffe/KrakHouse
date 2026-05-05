# 🚀 KrakHouse

Aplikacja pozwalająca wyszukiwać obszary miasta spełniające wymagania użytkowników

___

# 📦 Stack Technologiczny

## Backend

- **Java** 25
- **Spring Boot** 4.0.4
- **Gradle** - narzędzie do budowania
- **JUnit** - framework do testowania

- **Architektura heksagonalna**
- **Domain-Driven Design (DDD)**

### 📁 Struktura projektu

Struktura katalogów opiera się na architekturze heksagonalnej:

```text
Backend/src/main/java/pk/backend/
├── aplication/         # Warstwa aplikacji
│   ├── port/           # Porty
│   │   ├── inbound/    # Porty wejściowe (interfejsy dla kontrolerów)
│   │   └── outbound/   # Porty wyjściowe (interfejsy do komunikacji ze światem zewnętrznym)
│   └── service/        # Serwisy aplikacyjne implementujące porty wejściowe
├── domain/             # Warstwa domeny
│   ├── model/          # Modele domenowe
│   └── service/        # Logika biznesowa
└── infrastructure/     # Warstwa infrastruktury
    ├── adapter/        # Adaptery implementujące porty
    ├── controller/     # Adaptery wejściowe (kontrolery REST)
    └── dto/            # Obiekty transferu danych (Data Transfer Objects)
```

## Frontend

- **TypeScript** 5.9.3
- **React** 19.2.4
- **Vite** 8.0.1 - narzędzie do budowania i serwer deweloperski
- **React Leaflet** 5.0.0 - wrapper React dla biblioteki Leaflet
- **Leaflet** 1.9.4 - biblioteka do interaktywnych map

### 📁 Struktura projektu

```text
Frontend/src/
├── assets/            # Style, ikony, zasoby statyczne
├── components/        # Komponenty UI
│   ├── map/           # Mapa i warstwy
│   ├── filters/       # Panel filtrów
│   └── ui/            # Reużywalne elementy (przyciski, pola tekstowe)
├── view/              # Widok aplikacji
├── services/          # Komunikacja z backendem (API)
├── store/             # Zarządzanie stanem (Zustand)
├── types/             # Typy TypeScript
├── utils/             # Funkcje pomocnicze
└── App.tsx            # Główny komponent
```

### 🧠 Zarządzanie stanem (Zustand)

Aplikacja wykorzystuje bibliotekę Zustand do zarządzania globalnym stanem.

Stan globalny odpowiada za:
- wybrane filtry użytkownika
- zakresy wartości filtrów
- dane mapy pobrane z backendu
- stan ładowania (loading)
- błędy

### 🔄 Przepływ danych

1. Użytkownik zmienia filtr w UI
2. Komponent wywołuje `setFilters`
3. Następnie wywoływane jest `fetchMaps`
4. Store:
    - pobiera aktualne filtry (`get()`)
    - wysyła zapytanie do backendu
    - zapisuje wynik w `mapData`
5. Komponent mapy automatycznie się renderuje z nowymi danymi

___

# ⚙️ Instalacja i uruchomienie aplikacji

## Wymagania
- Docker
- Docker Compose

## Pobranie projektu

```bash
git clone https://github.com/Ereffe/KrakHouse.git
cd KrakHouse
```

## Uruchomienie

Po pobraniu projektu uruchom poniższe polecenie w głównym katalogu projektu:

```bash
docker compose up
```

Docker Compose automatycznie:
- Zbuduje wszystkie obrazy
- Zainstaluje wszystkie zależności
- Uruchomi wszystkie potrzebne serwisy

Aplikacja zostanie uruchomiona na:
- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:5173

## Zatrzymanie

```bash
docker-compose down
```

## Rebuild obrazów

```bash
docker-compose up --build
```

___

# 📚 Dokumentacja API

## Endpointy Mapy

### Pobierz dostępne filtry

**Get Filters**

* **URL:** `/filters`
* **Metoda HTTP:** `GET`
* **Opis:** Zwraca listę dostępnych filtrów do wyszukiwania obszarów

* **Odpowiedź Sukcesu:**
    * **Kod:** 200
    * **Zawartość:**
```json
[
  "airQuality",
  "crime",
  "noise",
  "price"
]
```

* **Odpowiedź Błędu:**
    * **Kod:** 500
      * **Zawartość:** `{ "message": "Serwis jest niedostępny" }`

---

### Pobierz mapy

**Get Merged Maps**

* **URL:** `/maps`
* **Metoda HTTP:** `GET`
* **Parametry:**
  * `filteredMaps` (wymagane) - Lista filtrów do zastosowania przy tworzeniu map
    * `type` - Typ mapy (`airQuality`, `crime`, `noise`, `price`)
    * `lowerBound` (opcjonalne) - Dolna granica zakresu wartości
    * `upperBound` (opcjonalne) - Górna granica zakresu wartości

* **Przykładowe Żądanie:**
```
GET /maps?filteredMaps={"type":"airQuality","lowerBound":100,"upperBound":500}&filteredMaps={"type":"crime","lowerBound":20,"upperBound":60}
```

* **Odpowiedź Sukcesu:**
    * **Kod:** 200
    * **Zawartość:**
```json
{
  "type": [
    {
      "type": "airQuality",
      "lowerBound": 100,
      "upperBound": 500
    },
    {
      "type": "crime",
      "lowerBound": 20,
      "upperBound": 60
    }
  ],
  "data": [
    [false, true, false, false, true, false],
    [false, true, false, false, true, false],
    [true, true, false, true, false, false],
    [false, true, true, false, true, false],
    [true, false, false, false, false, true],
    [false, true, true, true, false, true]
  ]
}
```

* **Odpowiedź Błędu:**
    * **Kod:** 400
      * **Zawartość:** `{ "message": "Nieprawidłowy typ mapy" }`
    * **Kod:** 503
      * **Zawartość:** `{ "message": "Zewnętrzny serwis jest niedostępny" }`
    * **Kod:** 500
      * **Zawartość:** `{ "message": "Serwis jest niedostępny" }`

---

### Pobierz filtrowaną mapę

**Get Filtered Map**

* **URL:** `/maps-list`
* **Metoda HTTP:** `GET`
* **Parametry:**
  * `filteredMaps` (wymagane) - Lista typów map, jakie chcemy otrzymać wraz z zakresami wartości
    * `type` - Typ mapy do filtrowania (`airQuality`, `crime`, `noise`, `price`)
    * `lowerBound` (opcjonalne) - Dolna granica zakresu wartości
    * `upperBound` (opcjonalne) - Górna granica zakresu wartości

* **Przykładowe Żądanie:**
```
GET /maps-list?filteredMaps={"type":"airQuality","lowerBound":100,"upperBound":500}&filteredMaps={"type":"crime","lowerBound":20,"upperBound":60}
```

* **Odpowiedź Sukcesu:**
    * **Kod:** 200
    * **Zawartość:**
```json
{
  "maps": [
    {
      "type": "airQuality",
      "value-type": "Air Quality Index (AQI)",
      "data": [
        [null, null, 234, null, 456, 123],
        [null, 234, null, null, 345, null],
        [123, 456, null, 234, null, null],
        [null, 123, 456, null, 234, null],
        [345, null, null, null, null, 123],
        [null, 234, 345, 456, null, 234]
      ]
    },
    {
      "type": "crime",
      "value-type": "Percentage %",
      "data": [
        [45, null, 23, null, 45, null],
        [null, 23, 56, null, 34, null],
        [null, 45, null, 23, 56, null],
        [null, null, 45, null, 23, 56],
        [34, null, null, 56, null, null],
        [null, 23, 34, 45, null, 23]
      ]
    }
  ]
}

```

* **Odpowiedź Błędu:**
    * **Kod:** 400
        * **Zawartość:** `{ "message": "Nieprawidłowy typ mapy", "invalidTypes": ["invalidType1", "invalidType2"] }`
    * **Kod:** 503
        * **Zawartość:** `{ "message": "Zewnętrzny serwis jest niedostępny" }`
    * **Kod:** 500
        * **Zawartość:** `{ "message": "Serwis jest niedostępny" }`

---

# 🚀 Rozbudowa aplikacji

## Tworzenie Nowych Filtrów

### 1. Utworzenie Nowego Typu Siatki Mapy

Rozpocznij od zdefiniowania, w jaki sposób wartości Twojego filtra są reprezentowane na mapie. Utwórz nową klasę implementującą interfejs `BoxValue`. Jej celem jest reprezentowanie pojedynczego pola na mapie.

**Interfejs:** `java/pk/backend/domain/model/box/BoxValue.java`

    public interface BoxValue {
        Object getValue();
        int compareTo(BoxValue other);
    }

`Object getValue()` - metoda ta wykorzystywana jest jedynie do automatycznej serializacji danych wyjściowych do formatu JSON.
`int compareTo(BoxValue other)` - służy do porównywania dwóch obiektów tego samego typu. Jest ona wymagana do poprawnego działania filtrowania mapy.

*Twój nowy filtr musi dostarczyć własną implementację tego interfejsu, definiując logikę porównywania i przechowywania unikalnego typu danych dla siatki.*

### 2. Utworzenie Portu Wyjściowego (Outbound Port)

Dla zachowania czystości domeny logika pobierania danych zewnętrznych musi odbywać się poprzez interfejs. Zdefiniuj port wyjściowy w warstwie aplikacji. Interfejs ten powinien deklarować metodę odpowiedzialną za pobieranie niezbędnych danych wejściowych potrzebnych do wygenerowania mapy.

**Lokalizacja Portu:** `java/pk/backend/aplication/port/outbound`

### 3. Implementacja Serwisu Domenowego (Domain Service)

Utwórz nowy serwis domenowy dedykowany dla Twojego filtra. Będzie on odpowiedzialny za zwracanie mapy danego typu, jak również za zwracanie informacji o istnieniu filtra danego typu.

* *UWAGA!!!* - Aby typ filtra został rozpoznany przez aplikację, serwis musi zostać oznaczony adnotacją `@Service` oraz musi implementować interfejs `MapService`.

**Interfejs:** `java/pk/backend/domain/service/MapService.java`

    public interface MapService {
        CityMap createMap(String mapType);
        String getType();
    }

*Twoja nowa klasa serwisu (`np. NoiseMapService`) powinna:*
*   *Posiadać wstrzykniętą zależność do portu wyjściowego utworzonego w Kroku 2.*
*   *Implementować logikę tworzenia mapy, wykorzystując wstrzyknięty port*
*   *Zwracać unikalny identyfikator w metodzie `getType()`.*

### 4. Implementacja Adaptera Infrastruktury (Infrastructure Adapter)

Ostatnim krokiem jest implementacja interfejsu portu wyjściowego (Krok 2) w warstwie infrastruktury. Ten adapter jest odpowiedzialny za fizyczne operacje wejścia/wyjścia (np. zapytania HTTP do zewnętrznego serwisu).

Adapter pobiera dane, ewentualnie je transformuje i zwraca do serwisu domenowego za pośrednictwem zdefiniowanego kontraktu.

**Lokalizacja Adaptera:** `java/pk/backend/infrastructure/adapter`