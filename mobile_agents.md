# Instrukcje dla Agenta Mobilnego (SportApp Mobile)

## Przeznaczenie Aplikacji
Aplikacja mobilna służy wyłącznie jako centrum przeglądania i analizy danych treningowych przesyłanych z zegarka Wear OS. Nie służy do nagrywania treningów bezpośrednio na telefonie.

## Kluczowe Założenia
- **Tylko Odczyt i Analiza**: Interfejs skoncentrowany na wykresach, statystykach i historycznych trasach (mapy).
- **Synchronizacja**: 
    - Automatyczna synchronizacja z zegarkiem w regularnych odstępach czasu (np. przy każdym uruchomieniu aplikacji lub w tle).
    - Przycisk "Wymuś synchronizację" (Force Sync) dostępny dla użytkownika.
    - Wykorzystanie Wear OS Data Layer API do transferu plików CSV i ustawień.
- **Material Design 3**: Pełne wykorzystanie Material 3 (M3), w tym Dynamic Color (jeśli dostępne) i nowoczesnych komponentów UI.
- **Architektura**: MVVM / Clean Architecture.
- **Baza danych**: Room (do keszowania danych z plików CSV w celu szybkiej analizy i filtrowania).

## Tech Stack (Mobile)
- UI: Jetpack Compose (Material 3)
- Nawigacja: Jetpack Navigation Component
- Wykresy: np. Vico lub Jetpack Compose Canvas dla dedykowanych wizualizacji
- Mapy: Google Maps SDK for Android (Compose Library)
- Przechowywanie: Room (statystyki), DataStore (ustawienia lokalne)
- Worker: WorkManager (do zadań synchronizacji w tle)

## Zasady UI/UX
- Ekran główny: Podsumowanie ostatniego treningu i ogólne statystyki tygodniowe.
- Lista treningów: Przejrzysta lista z filtrowaniem po typie sportu i dacie.
- Szczegóły treningu: 
    - Mapa z przebytą trasą.
    - Interaktywne wykresy tętna, prędkości i wysokości.
    - Szczegółowe metryki (kalorie, tempo, czas trwania).
- Spójność kolorystyczna: Motyw powinien nawiązywać do aplikacji na zegarku (akcenty szarości/czerwieni), ale w pełnym wydaniu M3.

## Komunikacja z Zegarkiem
- Implementacja `DataClient` i `MessageClient`.
- Obsługa transferu plików (Asset/File) dla logów CSV.
- Synchronizacja profilu zdrowotnego (zmiana na telefonie powinna trafić na zegarek i odwrotnie).

## Ważne
- Wszystkie operacje na plikach CSV muszą być asynchroniczne.
- Należy obsłużyć stany braku połączenia z zegarkiem.
- Logi z zegarka po przetworzeniu do bazy Room powinny być odpowiednio oznaczone, aby uniknąć duplikatów.
