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
    - [ ] **Filtracja sygnałów**: Implementacja filtrów (np. Moving Average) dla danych GPS i barometru (eliminacja szumów).
    - [ ] **Przegląd sensorów**: Sprawdzić jakie czujniki mają zegarki, które nie są jeszcze wykorzystane (np. temperatura, tlen we krwi).
- [x] **Własne dyscypliny**: Możliwanie dodawania własnych rodzajów sportu z wyborem aktywnych czujników, ich widoczności i zapisu.

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
    - [x] **Zarządzanie aktywnościami**: Dodanie opcji trwałego usuwania aktywności z historii.
    - [x] **Zaokrąglanie danych**: Upewnienie się, że wszystkie pola liczbowe w tabeli `workouts` mają zaokrąglenie do 2 miejsc po przecinku.
- [x] **Globalne Stałe**: Dodano plik `AppConstants.kt` ze zmiennymi, gdzie są definie z całej aplikacji (np. czułość kółka).
- [x] **Logowanie treningów**: Rejestracja parametrów co sekundę do bazy danych (metry, zaokrąglone) zgodnie z flagami zapisu.
- [x] **Podsumowanie zbiorcze**: Dopisywanie wyników do bazy danych po zakończeniu treningu.

## 3. Komunikacja międzyurządzeniowa
- [x] **Aplikacja na telefon (Mobile App)**:
    - [x] Stworzenie szkieletu interfejsu (Material 3).
    - [x] Synchronizacja `Wearable Data Layer API` (treningi i definicje sportów).
    - [x] Wyświetlanie statystyk i listy aktywności.
- [ ] **Analiza danych (Mobile)**:
    - [ ] **Szczegóły aktywności (Ekran ActivityDetailScreen)**:
        - [x] Wykresy (bpm, kroki, prędkość, wysokość itd.) z markerami.
        - [ ] Interaktywna mapa trasy z oznaczeniem startu (kółko) i końca (flaga).
        - [x] Personalizacja widoku (wybór i kolejność wykresów/mapy).
        - [x] Wybór koloru śladu na mapie.
        - [x] **Wykresy per aktywność**: Rozszerzenie opcji aktywności o możliwość definiowania widocznych wykresów dla konkretnego sportu.
        - [x] **Widgety per aktywność**: Możliwość wyboru i ustawienia kolejności kafelków podsumowania niezależnie dla każdego sportu.
        - [x] **Rozbudowa metryk sesji**: Dodanie Max HR, Max Speed, średniego tempa, przewyższeń i kadencji.
        - [x] **Pełna synchronizacja wykresów**: Wszystkie kolumny z `workout_points` are teraz poprawnie mapowane i wyświetlane na wykresach.
        - [ ] **Najlepsze tempo na km (Best Split)**: Automatyczne wykrywanie najszybszego odcinka 1km.
        - [ ] **Wykres 'Średnia długość kroku w czasie'**: Nowa wizualizacja techniki biegu.
        - [ ] **Optymalizacja wykresów**: Implementacja próbkowania (np. LTTB) do max 500 punktów.
        - [ ] **Przerób wykres 'kroki na min'**: Optymalizacja i czytelność wykresu kadencji.
        - [ ] **Weryfikacja obliczeń**: Sprawdzenie poprawności wyliczania `avgPace` oraz `avgStepLength`.
        - [x] **Opcja przycinania treningu**: Możliwość usunięcia niechcianych fragmentów na początku lub końcu sesji.
        - [x] **Aktualizacja statystyk po przycięciu**: Nowe wartości są przeliczane i odświeżane w czasie rzeczywistym na liście.
    - [ ] **Analiza stref tętna**: Podział treningu na strefy (Z1-Z5) i ich wizualizacja na ekranie szczegółów.
    - [x] **Naprawa danych w widgetach**: Statystyki na ekranie głównym i ogólnych statystykach are teraz poprawnie wyliczane i wyświetlane.
    - [x] Filtrowanie statystyk ogólnych (Ekran OverallStatsScreen).
    - [ ] **Naprawa filtrów**: Poprawa działania filtrów na liście aktywności (ActivityList).
- [x] **Ustawienia i Personalizacja (Mobile)**:
    - [x] Konfiguracja widgetów na ekranie głównym (wybór, kolejność).
    - [x] Wybór okresu raportowania (dziś, tydzień, m-c, rok).
    - [x] **Poprawa Layoutu**: Przesunięcie tytułów ekranów opcji w dół (aby nie chowały się za kamerką).
    - [x] **Nawigacja**: Dodanie przycisku powrotu w TopAppBar na ekranach opcji i definie aktywności.
    - [ ] **Odświeżenie UI**: Zmiana wyglądu tabel/list (dodano paski przewijania) oraz wdrożenie sliderów do regulacji parametrów.
    - [x] **Branding**: Dodanie oficjalnego logo aplikacji (wewnątrz apki oraz ikony systemowe Adaptive Icons).
    - [ ] **Więcej widgetów**: Dodanie dodatkowych typów widgetów do wyboru w ustawieniach strony głównej.

## 4. Funkcje Wear OS (Zegarek)
- [x] **Statystyki na zegarku**:
    - [x] Wyświetlanie podsumowania widgetów z ostatniego tygodnia (dystans, kcal, kroki).
- [ ] **Optymalizacja UI**:
    - [x] **Responsywność korony zegarka**: Zwiększenie czułości kółka przy przewijaniu długich list treningowych.
    - [x] **Dynamiczny układ treningu**: Mapa wyświetlana na końcu listy czujników (płynne przewijanie).
    - [ ] **Obsługa Ambient Mode**: Implementacja trybu oszczędzania energii dla ekranów treningowych.
    - [ ] **Centrowanie mapy**: Automatyczne odświeżanie mapy tak, aby kropka pozycji była zawsze w centrum.

## 5. Chmura i Bezpieczeństwo
- [ ] **Integracja z Google**:
    - [ ] Dodanie logowania przez konto Google.
    - [ ] **Integracja z Google Drive**: Zapis historii aktywności i podsumowań w chmurze (backup/sync).
    - [ ] **Health Connect**: Synchronizacja danych treningowych z Google Health Connect.

## 6. Jakość i Testy
- [x] **Unit testy**:
    - [x] Testy logiki biznesowej (Kalkulatory, Repozytoria, ViewModele).
    - [x] **Testy edycji treningu**: Weryfikacja logiki przycinania i przeliczania statystyk w `ActivityTrimViewModel`.
- [x] **Testy komponentów UI**:
    - [x] Weryfikacja wyświetlania wykresów i filtrowania (OverallStats).
    - [ ] **Testy ekranu szczegółów**: Weryfikacja interakcji z wykresami i mapą.

## 7. Refaktoryzacja i Dobre Praktyki
- [x] **Wstrzykiwanie Zależności**: Wdrożenie **Hilt** w modułach `mobile` i `wear`.
- [x] **Migracja na KSP**: Przejście z Kapt na KSP w module `mobile`.
- [x] **Usunięcie manualnych fabryk ViewModeli**: Pełne przejście na `@HiltViewModel`.
- [x] **Cleanup**: Usunięcie trybu deweloperskiego oraz nieużywanych mechanizmów obsługi plików CSV (zrealizowane).
- [ ] **Internacjonalizacja**: Dodanie wsparcia dla wielu języków (tłumaczenia).
- [ ] **Clean Code**: Usunięcie hardkodowanych stringów (trasy nawigacji) do stałych/typów enumeratywnych.

## PRIORYTETY
1. **Weryfikacja obliczeń** - Sprawdzenie poprawności `avgPace` i `avgStepLength`.
2. **Widget 'Best Split' i Wykres długości kroku** - Implementacja zaawansowanej analizy techniki biegu.
3. **Analiza stref tętna** - Wyliczanie i prezentacja czasu w strefach Z1-Z5.
4. **Interaktywna mapa trasy** - Dodanie mapy do ekranu szczegółów aktywności z oznaczeniem startu i końca trasy.
