# Lista Funkcjonalności do Zakodowania - SportApp

## 1. Integracja z sensorami (Wear OS)
- [x] **Odczytywanie wartości z czytników**:
    - [x] Tętno (Heart Rate) w czasie rzeczywistym.
    - [x] Licznik kroków (Step Counter).
    - [x] Obliczanie odległości na podstawie kroków i GPS.
    - [x] Wyliczanie spalonych kalorii (Modele: Keytel, MET, HRR).
    - [x] Śledzenie trasy (GPS Location tracking).
- [ ] **Sensory i Precyzja**:
    - [x] **Wprowadzenie Foreground Service**: Przeniesienie logiki sesji z UI do usługi w tle (zapobieganie ubijaniu treningu).
    - [ ] **Centralny PermissionManager**: Ujednolicona obsługa uprawnień przed startem aktywności.
    - [x] **Filtracja sygnałów**: Implementacja filtrów (np. Moving Average) dla danych tętna.
    - [x] **Przegląd sensorów**: Sprawdzono i dodano obsługę SpO2 oraz ciśnienia atmosferycznego (barometru).
    - [x] **Hybrydowa kalibracja wysokości**: GPS offset dla barometru w celu stabilizacji wysokości startowej.
- [x] **Własne dyscypliny**: 
    - [x] Możliwość dodawania własnych rodzajów sportu z wyborem aktywnych czujników, ich widoczności i zapisu.
    - [x] Zarządzanie kolejnością dyscyplin na liście (Mobile & Wear).

## 2. Zarządzanie danymi i plikami
- [x] **Zapis konfiguracji aplikacji**:
    - [x] Automatyczne zapisywanie i wczytywanie ustawień (mapa, zegar, dane zdrowotne).
- [x] **Konfiguracja aktywności (XML)**:
    - [x] Stworzenie parsera XML dla układu sensorów.
- [ ] **Optymalizacja i Architektura danych**:
    - [x] **Refaktoryzacja IWorkoutRepository**: Ujednolicona obsługa uprawnień przed startem aktywności.
    - [x] **Przechowywanie danych**: Pełna migracja na bazę danych Room.
    - [x] **Asynchroniczny zapis (IO)**: Przeniesienie operacji dyskowych w `WorkoutLogger` na `Dispatchers.IO`.
    - [x] **Migracja na bazę danych (Room)**: Wdrożenie bazy danych Room (`AppDatabase`, `WorkoutDao`) dla efektywnego zarządzania danymi sesji.
    - [x] **Refaktoring Tętna**: Usunięcie kolumny `avgBpm` z tabeli `workouts` i zastąpienie jej dynamicznym obliczaniem średniej z `workout_points`.
    - [x] **Zarządzanie aktywnościami**: Dodanie opcji trwalego usuwania aktywności z historii.
    - [x] **Zaokrąglanie danych**: Upewnienie się, że wszystkie pola liczbowe w tabeli `workouts` mają zaokrąglenie do 2 miejsc po przecinku.
- [x] **Globalne Stałe**: Dodano plik `AppConstants.kt` ze zmiennymi, gdzie are definie z całej aplikacji (np. czułość kółka).
- [x] **Logowanie treningów**: Rejestracja parametrów co sekundę do bazy danych (metry, zaokrąglone) zgodnie z flagami zapisu.
- [x] **Podsumowanie zbiorcze**: Dopisywanie wyników do bazy danych po zakończeniu treningu.

## 3. Komunikacja międzyurządzeniowa
- [x] **Aplikacja na telefon (Mobile App)**:
    - [x] Stworzenie szkieletu interfejsu (Material 3).
    - [x] Synchronizacja `Wearable Data Layer API` (treningi, definicje sportów, ustawienia statystyk).
    - [x] Wyświetlanie statystyk i listy aktywności.
- [ ] **Analiza danych (Mobile)**:
    - [ ] **Szczegóły aktywności (Ekran ActivityDetailScreen)**:
        - [x] Wykresy (bpm, kroki, prędkość, wysokość itd.) z markerami.
        - [x] Interaktywna mapa trasy z odcinkami (Laps).
        - [x] Personalizacja widoku (wybór i kolejność wykresów/mapy).
        - [x] Wybór koloru śladu na mapie.
        - [x] **Wykresy per aktywność**: Rozszerzenie opcji aktywności o możliwość definiowania widocznych wykresów dla konkretnego sportu.
        - [x] **Widgety per aktywność**: Możliwość wyboru i ustawienia kolejności kafelków podsumowania niezależnie dla każdego sportu.
        - [x] **Rozbudowa metryk sesji**: Dodanie Max HR, Max Speed, średniego tempa, przewyższeń i kadencji.
        - [x] **Pełna synchronizacja wykresów**: Wszystkie kolumny z `workout_points` are teraz poprawnie mapowane i wyświetlane na wykresach.
        - [ ] **Najlepsze tempo na km (Best Split)**: Automatyczne wykrywanie najszybszego odcinka 1km.
        - [ ] **Wykres 'Średnia długość kroku w czasie'**: Nowa wizualizacja techniki biegu.
        - [ ] **Optymalizacja wykresów**: Implementacja próbkowania (np. LTTB) do max 500 punktów.
        - [x] **Przerób wykres 'kroki na min'**: Optymalizacja i czytelność wykresu kadencji (wdrożono wygładzanie).
        - [x] **Weryfikacja obliczeń**: Sprawdzenie poprawności wyliczania `avgPace` oraz `avgStepLength`.
        - [x] **Opcja przycinania treningu**: Możliwanie usunięcia niechcianych fragmentów na początku lub końcu sesji.
        - [x] **Aktualizacja statystyk po przycięciu**: Nowe wartości are przeliczane i odświeżane w czasie rzeczywistym na liście.
    - [x] **Analiza stref tętna**: Podział treningu na strefy (Z1-Z5), wizualizacja kołowa i tabela na ekranie szczegółów.
    - [x] **Naprawa danych w widgetach**: Statystyki na ekranie głównym i ogólnych statystykach are teraz poprawnie wyliczane i wyświetlane.
    - [x] Filtrowanie statystyk ogólnych (Ekran OverallStatsScreen).
    - [x] **Naprawa filtrów**: Poprawa działania filtrów na liście aktywności (ActivityList) - zaimplementowano reaktywne filtrowanie po typie i dacie.
- [x] **Ustawienia i Personalizacja (Mobile)**:
    - [x] Konfiguracja widgetów na ekranie głównym (wybór, kolejność).
    - [x] Wybór okresu raportowania (dziś, tydzień, m-c, rok).
    - [x] **Zdalna konfiguracja zegarka**: Możliwość ustawienia pól statystyk i okresu dla zegarka z poziomu aplikacji mobilnej.
    - [x] **Dynamiczny system zmiany języka**:
        - [x] Implementacja `AppStrings` i `CompositionLocal` dla dynamicznych tłumaczeń bez restartu Activity.
        - [x] Obsługa 7 języków (PL, EN, ES, PT, DE, FR, IT).
        - [x] Synchronizacja języka z Mobile na Wear OS.
        - [x] Ekran wyboru języka z flagami w ustawieniach.
    - [x] **Poprawa Layoutu**: Przesunięcie tytułów ekranów opcji w dół (aby nie chowały się za kamerką).
    - [x] **Nawigacja**: Dodanie przycisku powrotu w TopAppBar na ekranach opcji i definie aktywności.
    - [x] **Odświeżenie UI**: Zmiana wyglądu tabel/list (dodano paski przewijania) oraz wdrożenie sliderów do regulacji parametrów.
    - [x] **Branding**: Dodanie oficjalnego logo aplikacji (wewnątrz apki oraz ikony systemowe Adaptive Icons).
    - [ ] **Więcej widgetów**: Dodanie dodatkowych typów widgetów do wyboru w ustawieniach strony głównej.

## 4. Funkcje Wear OS (Zegarek)
- [x] **Statystyki na zegarku**:
    - [x] Dynamiczne wyświetlanie widgetów (dystans, kcal, kroki, przewyższenia itp.) zgodnie z ustawieniami z telefonu.
    - [x] Obsługa różnych okresów raportowania synchronizowanych z Mobile.
- [ ] **Optymalizacja UI**:
    - [x] **Responsywność korony zegarka**: Zwiększenie czułości kółka przy przewijaniu długich list treningowych.
    - [x] **Dynamiczny układ treningu**: Mapa wyświetlana na końcu listy czujników (płynne przewijanie).
    - [x] **Obsługa Ambient Mode**: Implementacja trybu oszczędzania energii dla ekranów treningowych.
    - [x] **Centrowanie mapy**: Automatyczne odświeżanie mapy tak, aby kropka pozycji była zawsze w centrum.

## 5. Chmura i Bezpieczeństwo
- [ ] **Integracja z Google**:
    - [ ] Dodanie logowania przez konto Google.
    - [ ] **Integracja z Google Drive**: Zapis historii aktywności i podsumowań w chmurze (backup/sync).
    - [ ] **Health Connect**: Synchronizacja danych treningowych z Google Health Connect.
-
### Ustawienia i Personalizacja (Mobile)
- [x] Implementacja dynamicznego Dark Mode (Material 3)
    - [x] Zarządzanie motywem w `MobileSettingsManager` (DataStore)
    - [x] Definicja palet `lightColorScheme` i `darkColorScheme` (AMOLED-friendly)
    - [x] Reaktywność UI na zmianę motywu (`collectAsStateWithLifecycle`)
    - [x] Dostosowanie wykresów trendów i szczegółów aktywności (Vico Charts)
    - [x] Dodanie opcji wyboru motywu w `SettingsScreen`
- [x] Implementacja Google Maps Dark Mode (MapStyleOptions)
- [ ] Widgety na ekranie głównym (Glance)
- [ ] Personalizacja jednostek (Metric/Imperial)
- [x] Ikonki partnerów (mrf, emeref) dostosowane do Dark Mode

## 6. Jakość i Testy
- [x] **Unit testy**:
    - [x] Testy logiki biznesowej (Kalkulatory, Repozytoria, ViewModele).
    - [x] **Testy edycji treningu**: Weryfikacja logiki przycinania i przeliczania statystyk w `ActivityTrimViewModel`.
- [ ] **Testy komponentów UI**:
    - [x] Weryfikacja wyświetlania wykresów i filtrowania (OverallStats).
    - [ ] **Testy ekranu szczegółów**: Weryfikacja interakcji z wykresami i mapą.
- [ ] **Testy stabilności**:
    - [ ] **Sprawdź działanie aplikacji na 'pauzie'**: Weryfikacja poprawnego wstrzymywania i wznawiania zliczania danych (kroki, dystans, czas).

### Statystyki i Wykresy
- [x] Wykresy trendów in `OverallStatsScreen` (Vico)
- [x] Wykresy tętna i strefy in `ActivityDetailScreen`
- [x] **Porównywanie dwóch aktywności na jednym wykresie** (ActivityCompareScreen)
- [x] **Eksport i Import danych (GPX)**:
    - [x] Eksport aktywności do formatu GPX z danymi HR i kadencji.
    - [x] Pakowanie wielu plików do ZIP przy eksporcie zbiorczym.
    - [x] **Import plików GPX**: Pełne przeliczanie statystyk, mapowanie punktów i generowanie okrążeń.
    - [x] **Walidacja duplikatów**: System wykrywania powtarzających się sesji przy imporcie.
    - [x] **Personalizacja kalorii**: Wyliczanie spalania na podstawie tętna i profilu zdrowotnego użytkownika (HRR).

## PRIORYTETY
1. **Najlepsze tempo na km (Best Split)** - PRIORYTET: WYSOKI - Automatyczne wykrywanie najszybszego odcinka 1km w sesji.
2. **Sprawdź działanie aplikacji na 'pauzie'** - PRIORYTET: WYSOKI - Dokładna weryfikacja zliczania danych (kroki/dystans) podczas wstrzymania treningu.
3. **Integracja z Google Drive / Health Connect** - PRIORYTET: ŚREDNI - Synchronizacja danych z ekosystemem Google.
4. **Widgety na ekranie głównym (Glance)** - PRIORYTET: NISKI - Dodanie wsparcia dla widgetów systemowych Androida.
