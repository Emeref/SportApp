# Lista Funkcjonalności do Zakodowania - SportApp

## 1. Integracja z sensorami (Wear OS)
- [x] **Odczytywanie wartości z czytników**:
    - [x] Tętno (Heart Rate) w czasie rzeczywistym.
    - [x] Licznik kroków (Step Counter).
    - [x] Obliczanie odległości na podstawie kroków i GPS.
    - [x] Wyliczanie spalonych kalorii (Modele: Keytel, MET, HRR).
    - [x] Śledzenie trasy (GPS Location tracking).
- [ ] **Stabilność i Precyzja**:
    - [ ] **Wprowadzenie Foreground Service**: Przeniesienie logiki sesji z UI do usługi w tle (zapobieganie ubijaniu treningu).
    - [ ] **Centralny PermissionManager**: Ujednolicona obsługa uprawnień przed startem aktywności.
    - [ ] **Filtracja sygnałów**: Implementacja filtrów (np. Moving Average) dla danych GPS i barometru (eliminacja szumów).

## 2. Zarządzanie danymi i plikami
- [x] **Zapis konfiguracji aplikacji**:
    - [x] Automatyczne zapisywanie i wczytywanie ustawień (mapa, zegar, dane zdrowotne).
- [x] **Konfiguracja aktywności (XML)**:
    - [x] Stworzenie parsera XML dla układu sensorów.
- [ ] **Optymalizacja i Architektura danych**:
    - [x] **Refaktoryzacja IWorkoutRepository**: Ujednolicenie interfejsu na metody `suspend`.
    - [x] **Strumieniowy odczyt CSV**: Zamiana `readLines()` na `BufferedReader` (bezpieczeństwo pamięci RAM przy dużych plikach).
    - [ ] **Asynchroniczny zapis (IO)**: Przeniesienie operacji dyskowych w `WorkoutLogger` na `Dispatchers.IO`.
- [x] **Logowanie treningów (CSV)**:
    - [x] Rejestracja parametrów co sekundę do pliku sesji (metry, zaokrąglone).
    - [x] Obliczanie średniego BPM, przewyższeń i dystansu.
- [x] **Podsumowanie zbiorcze (CSV)**:
    - [x] Dopisywanie wyników do `Podsumowanie_cwiczen.csv`.

## 3. Komunikacja międzyurządzeniowa
- [x] **Aplikacja na telefon (Mobile App)**:
    - [x] Stworzenie szkieletu interfejsu (Material 3).
    - [x] Synchronizacja `Wearable Data Layer API`.
    - [x] Wyświetlanie statystyk i listy aktywności.
- [ ] **Analiza danych (Mobile)**:
    - [ ] **Szczegóły aktywności (Ekran ActivityDetailScreen)**:
        - [x] Wykresy (bpm, kroki, prędkość, wysokość itd.) z markerami.
        - [ ] Interaktywna mapa trasy z oznaczeniem startu (kółko) i końca (flaga).
        - [x] Personalizacja widoku (wybór i kolejność wykresów/mapy).
        - [x] Wybór koloru śladu na mapie.
        - [ ] **Optymalizacja wykresów**: Implementacja próbkowania (np. LTTB) do max 500 punktów.
    - [x] Filtrowanie statystyk ogólnych (Ekran OverallStatsScreen).
    - [ ] **Naprawa filtrów**: Poprawa działania filtrów na liście aktywności (ActivityList).
- [x] **Ustawienia i Personalizacja (Mobile)**:
    - [x] Konfiguracja widgetów na ekranie głównym (wybór, kolejność).
    - [x] Wybór okresu raportowania (dziś, tydzień, m-c, rok).

## 4. Funkcje Wear OS (Zegarek)
- [x] **Statystyki na zegarku**:
    - [x] Wyświetlanie podsumowania widgetów z ostatniego tygodnia (dystans, kcal, kroki).
- [ ] **Optymalizacja UI**:
    - [ ] **Obsługa Ambient Mode**: Implementacja trybu oszczędzania energii dla ekranów treningowych.

## 5. Chmura i Bezpieczeństwo
- [ ] **Integracja z Google**:
    - [ ] Dodanie logowania przez konto Google.
    - [ ] **Integracja z Google Drive**: Zapis historii aktywności i podsumowań w chmurze (backup/sync).

## 6. Jakość i Testy
- [x] **Unit testy**:
    - [x] Testy logiki biznesowej (Kalkulatory, Repozytoria, ViewModele).
    - [ ] **Testy parsera CSV**: Weryfikacja czytania dużych plików sesji.
- [x] **Testy komponentów UI**:
    - [x] Weryfikacja wyświetlania wykresów i filtrowania (OverallStats).
    - [ ] **Testy ekranu szczegółów**: Weryfikacja interakcji z wykresami i mapą.

## 7. Refaktoryzacja i Dobre Praktyki
- [x] **Wstrzykiwanie Zależności**: Wdrożenie **Hilt** w modułach `mobile` i `wear`.
- [x] **Migracja na KSP**: Przejście z Kapt na KSP w module `mobile` (lepsza kompatybilność z Kotlin 2.x i Hilt).
- [x] **Usunięcie manualnych fabryk ViewModeli**: Pełne przejście na `@HiltViewModel`.
- [ ] **Clean Code**: Usunięcie hardkodowanych stringów (klucze CSV, trasy nawigacji) do stałych/typów enumeratywnych.
- [ ] **Usprawnienie modelu danych**: Rozważenie przejścia z metadanych w nazwie pliku na metadane wewnątrz pliku lub bazę Room.
