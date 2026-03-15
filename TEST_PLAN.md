# Plan Testów - SportApp

## Zasady Ogólne (Quality Gate)
- **Obowiązek Testowania**: Każdy nowy feature musi posiadać testy jednostkowe lub integracyjne.
- **Regresja**: Przed mergem/zatwierdzeniem zmian wszystkie testy (`./gradlew test`) muszą przechodzić na zielono.
- **Środowisko**: Testy jednostkowe uruchamiane na JVM (Robolectric dla zależności Androidowych).

## 1. Unit Testy (Logika Biznesowa)
- **CalorieCalculator**:
    - Weryfikacja poprawności obliczeń dla modelu HRR (Heart Rate Reserve).
    - Testy brzegowe: tętno poniżej spoczynkowego (0 kcal), tętno równe maksymalnemu, zerowa masa ciała.
- **WorkoutRepository**:
    - Testowanie filtrów dat (dzisiaj, tydzień, miesiąc, rok).
    - Weryfikacja formatowania dystansu (m -> km).
- **Parsery XML**:
    - Test poprawności odczytu konfiguracji sensorów z plików XML.

## 2. Testy Integracyjne (Data Layer & Storage)
- **DataStore (MobileSettingsManager)**:
    - Zapis i odczyt ustawień widgetów.
    - *Izolacja*: Każdy test musi czyścić instancję DataStore.
- **Wear OS Data Layer**:
    - Testy przesyłania wiadomości i danych między urządzeniami.

## 3. Testy UI & UX (Automatyczne - Espresso/Compose Test)
- **Ekran Główny (Mobile)**:
    - Sprawdzenie czy widgety wyświetlają się w poprawnej kolejności.
    - Weryfikacja reakcji na zmianę okresu raportowania.
- **Ekran Statystyk Ogólnych**:
    - Testy interakcji z wykresem Vico (wyświetlanie markera po dotyku).
- **Wear UI**:
    - Weryfikacja `ScalingLazyColumn` i widoczności elementów w `Ambient Mode`.

## 4. Testy Manualne (Scenariusze)
- **Utrata sygnału GPS**: Czy aplikacja poprawnie loguje puste pola i czy sumuje dystans po odzyskaniu sygnału?
- **Brak prądu**: Czy dane treningu są bezpiecznie zapisane w bazie Room przy nagłym zamknięciu aplikacji?
- **Brak uprawnień**: Czy wyświetla się czytelny komunikat, gdy użytkownik nie nadał uprawnień?
- **Rotary Input**: Czy kółko zegarka poprawnie przewija listy w ustawieniach i na ekranie aktywności?

## 5. Testy Wydajnościowe i Baterii
- **Zapis Room**: Monitoring zużycia baterii przy długotrwałym (1h+) logowaniu danych z częstotliwością 1Hz.
- **Pamięć**: Weryfikacja czy buforowanie logów nie powoduje wycieków pamięci (Memory Leaks) przy wielogodzinnych treningach.
- **Synchronizacja w tle**: Wpływ przesyłania danych treningowych na responsywność aplikacji mobilnej.
