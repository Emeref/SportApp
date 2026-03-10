# Plan Testów - SportApp

## 1. Unit Testy (Logika Biznesowa)
- **CalorieCalculator**:
    - Weryfikacja poprawności obliczeń dla modelu HRR (Heart Rate Reserve).
    - Testy brzegowe: tętno poniżej spoczynkowego (0 kcal), tętno równe maksymalnemu, zerowa masa ciała.
- **WorkoutRepository**:
    - Testowanie filtrów dat (dzisiaj, tydzień, miesiąc, rok).
    - Testowanie sumowania statystyk z plików CSV.
    - Weryfikacja formatowania dystansu (m -> km).
- **Parsery XML**:
    - Test poprawności odczytu konfiguracji sensorów z plików XML.

## 2. Testy Integracyjne (Data Layer & Storage)
- **DataStore (MobileSettingsManager)**:
    - Zapis i odczyt ustawień widgetów.
    - Poprawność przełączania i trwałego zapisu flagi `useTestData`.
- **TestDataGenerator**:
    - Weryfikacja czy generator poprawnie tworzy strukturę plików w folderze `test_activities`.

## 3. Testy UI & UX (Automatyczne - Espresso/Compose Test)
- **Ekran Główny (Mobile)**:
    - Sprawdzenie czy widgety wyświetlają się w poprawnej kolejności.
    - Weryfikacja reakcji na zmianę okresu raportowania.
- **Ekran Statystyk Ogólnych**:
    - Testy interakcji z wykresem Vico (wyświetlanie markera po dotyku).
    - Filtrowanie listy aktywności po typie.

## 4. Testy Manualne (Scenariusze)
- **Utrata sygnału GPS**: Czy aplikacja poprawnie loguje puste pola i czy sumuje dystans po odzyskaniu sygnału?
- **Rozładowanie baterii**: Czy plik CSV zostaje poprawnie zamknięty i zapisany przed wyłączeniem się zegarka?
- **Brak uprawnień**: Czy wyświetla się czytelny komunikat, gdy użytkownik nie nadał uprawnień do tętna lub lokalizacji?
- **Rotary Input**: Czy kółko zegarka poprawnie przewija listy w ustawieniach i na ekranie aktywności?
