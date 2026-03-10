# Lista Funkcjonalności do Zakodowania - SportApp

## 1. Integracja z sensorami (Wear OS)
- [x] **Odczytywanie wartości z czytników**:
    - [x] Tętno (Heart Rate) w czasie rzeczywistym.
    - [x] Licznik kroków (Step Counter).
    - [x] Obliczanie odległości na podstawie kroków i GPS.
    - [x] Wyliczanie spalonych kalorii (Modele: Keytel, MET, HRR).
    - [x] Śledzenie trasy (GPS Location tracking).

## 2. Zarządzanie danymi i plikami
- [x] **Zapis konfiguracji aplikacji**:
    - [x] Automatyczne zapisywanie i wczytywanie ustawień (mapa, zegar, dane zdrowotne).
- [x] **Konfiguracja aktywności (XML)**:
    - [x] Stworzenie parsera XML dla układu sensorów.
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
        - [ ] **Optymalizacja wykresów**: Usprawnienie wyświetlania przy dużej liczbie rekordów (próbkowanie do max 500 punktów).
    - [x] Filtrowanie statystyk ogólnych (Ekran OverallStatsScreen).
    - [ ] **Naprawa filtrów**: Poprawa działania filtrów na liście aktywności (ActivityList).
- [x] **Ustawienia i Personalizacja (Mobile)**:
    - [x] Konfiguracja widgetów na ekranie głównym (wybór, kolejność).
    - [x] Wybór okresu raportowania (dziś, tydzień, miesiąc, rok, inne).

## 4. Funkcje Wear OS (Zegarek)
- [x] **Statystyki na zegarku**:
    - [x] Wyświetlanie podsumowania widgetów z ostatniego tygodnia (dystans, kcal, kroki).

## 5. Chmura i Bezpieczeństwo
- [ ] **Integracja z Google**:
    - [ ] Dodanie logowania przez konto Google.
    - [ ] **Integracja z Google Drive**: Zapis historii aktywności i podsumowań na koncie użytkownika, backup i wczytywanie danych.

## 6. Jakość i Testy
- [x] **Unit testy**:
    - [x] Testy logiki biznesowej (Kalkulatory, Repozytoria, ViewModele).
    - [ ] **Testy parsera CSV i Mapy**: Weryfikacja poprawności rysowania trasy i czytania dużych plików sesji.
- [x] **Testy komponentów UI**:
    - [x] Weryfikacja wyświetlania wykresów i filtrowania (OverallStats).
    - [ ] **Testy ekranu szczegółów**: Weryfikacja interakcji z wykresami i mapą.

## ZREALIZOWANE ELEMENTY INFRASTRUKTURY:
- [x] System nawigacji i zarządzanie sesją.
- [x] Synchronizacja plików CSV między zegarkiem a telefonem.
- [x] Material 3 UI na telefonie.
- [x] Dynamiczne formatowanie dystansu na ekranie głównym (m/km).
- [x] Ekran statystyk ogólnych z filtrowaniem i wykresami trendów.
- [x] Automatyzacja CI z cache'owaniem Gradle i Dependency Injection w testach.
