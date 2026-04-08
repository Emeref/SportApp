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
    - [x] **Zarządzanie aktywnościami**: Dodanie opcji trwałego usuwania aktywności z historii.
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
        - [x] **Wykres 'Średnia długość kroku w czasie'**: Nowa wizualizacja techniki biegu (z logiką fail-safe, wygładzaniem i ograniczeniem osi Y).
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
    - [x] Zarządzanie motywem in `MobileSettingsManager` (DataStore)
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


### Aktywność na komórce
Co ma się dziać:
- Na ekranie głownym jest znak plus
- klikniecie go przenosi na ekran 'Start' z lista dostepnych aktywnosci
- Odpalenie sportu bedzie pokazywać widgety (te same które sa juz zdefiniowane dla aktywnosci i pokazywane przy odpalaniu aktywnosci z poziomu wear)
- Jeśli jest na aktywnosci dostepna 'Mapa' to mapa ma się odpalać jako podstawowy ekran.
- pod mapa wyswietlac sie beda dwa pierwsze widgety, pozostałe widgety (jeśli jakies sa) to beda wyswietlac sie na kolejnej stronie
- Przy krawedzi ekranu mamy przycisk hamburger menu, ktory wysuwac bedzie boczne menu gdzie mozemy 'zakonczyc trening', 'zapauzowac trening', 'ustawic ekran komorki na niegasnacy'. 'ustawic ekran na przechodzacy w mode ambientu, gdzie pokazuja sie tylko szesc pierwszych widgetow
- Jesli aplikacja jest odpalona z komorki to chhce aby tetno bylo pobierane z zegarka,
- Wszystkie pozostale dane byly pobierane z komórki (chyba ze komorka nie ma takiego czujnika, wtedy z zegarka)
- Chce aby trasa na mapie pokazywała trase przebyta w ciagu ostatnich 60 sekund (60 rekordow z bazy)
- chce aby wszystko zapisywalo sie w bazie na komorce i synchronizowalo pozniej z zegarkiem
- chce aby na zegarku pokazywalo sie info, jakby zaczal sie trening i wskazywaly sie wszystkie zbierane dane.
- chce aby dalo sie wystartowac na komorce bez podpietego zegarka (i aby taka aktywnosc sie dobrze zapisala, tylko w takim wypadku bez czujnikow ktore sa tylko w zegarku)
- Dodatkowo chcę aby dało się wybrać na mapie cel, który bedzie zaznaczony na mapie na komorce razem z trasa do przebycia.
- [ ] po wybtraniu aktywnosci powinien odpalic sie ekran z mapa i widgetami, ale nie wystartowac aktywnosci.
  - Na tym ekranie powinny byc przeniesione przyciski: na poczatku widoczny zielony 'start' jak sie go kliknie to wyswietla sie duzy 'stop' na srodku i mniejsze 'pauza' i 'zablokuj' po bokacz
  - Przyciski powinny byc widoczne na dole jesli jest mapa to miedzy mapa a widgetami, jesli sa same widgety to pod widgetami
  - Przycisk stop przenosi na ekran podsumowania aktywnosci z wynikami stworzonymi na logice takiej samej jak mamy na zegarku w podsumowaniu tam
  - Przycisk 'zablokuj' blokuje dotyk na ekranie, aby nie kliknelo sie przypadkiem
  - po kliknieciu 'zablokuj' pojawia sie przycisk 'odblokuj' w jego miejscu, pozostale przyciski sa wyszarzone
  - klikniecie 'odblokuj' odblokowuje pozostale przyciski i aplikacje
  - Podczas zablokowania ciagle widac zmiany na mapie i w widgetach
- [ ] Ekran mapy sie ma generowac tylko tam gdzie mapa wystepuje, a nie w kazdym
- [ ]  przycisk '+/-' na mapie pozwala przyblizac i oddalac, nie ma automatycznego powrotu. Zostawiamy zblizenie wybrane przez uzytkownika
- [ ] Jak sie przesunie mape w jakikolwiek sposob to pojawia sie przycisk aktualizacji lokalizacji na lokalna
- [ ] Po kliknieciu w aktualizacje lokalizacji wracamy do wyswietlania obecnej lokalizacji i wracamy do automatycznego odswiezanai lokalizacji aby zawsze byc w centrum mapy (do momentu fizycznego przesuniecia oczywiscie)
- [ ] Jak wejdziemy w tryb ambient to wyswietlamy w tym trybie do momentu odznaczenia w opcjach
- [ ] tryb ambient ma w opcjach miec checkbox pokazujacy czy jest wlaczony czy nie
- [ ] dane ze sportu maja sie zapisywac w bazie
- [ ] upewnic sie ze puls jest sczytywany
- [ ] wybór celu na mapie z pokazywaniem czasu dojazdu


## PRIORYTETY
1. **Najlepsze tempo na km (Best Split)** - PRIORYTET: WYSOKI - Automatyczne wykrywanie najszybszego odcinka 1km w sesji.
2. **Sprawdź działanie aplikacji na 'pauzie'** - PRIORYTET: WYSOKI - Dokładna weryfikacja zliczania danych (kroki/dystans) podczas wstrzymania treningu.
3. **Integracja z Google Drive / Health Connect** - PRIORYTET: ŚREDNI - Synchronizacja danych z ekosystemem Google.
4. **Widgety na ekranie głównym (Glance)** - PRIORYTET: NISKI - Dodanie wsparcia dla widgetów systemowych Androida.
