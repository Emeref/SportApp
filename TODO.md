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
    - [ ] Szczegóły aktywności (Wykresy, Mapa trasy).
    - [ ] Filtrowanie statystyk ogólnych.
- [x] **Ustawienia i Personalizacja (Mobile)**:
    - [x] Konfiguracja widgetów na ekranie głównym (wybór, kolejność).
    - [x] Wybór okresu raportowania (dziś, tydzień, miesiąc, rok, inne).

## 4. Chmura i Bezpieczeństwo
- [ ] **Integracja z Google**:
    - [ ] Dodanie logowania przez konto Google.
    - [ ] **Integracja z Google Drive**: Zapis historii aktywności i podsumowań na koncie użytkownika, backup i wczytywanie danych.

## 5. Jakość i Testy
- [ ] **Unit testy**:
    - [ ] Testy logiki biznesowej.
    - [ ] Testy komponentów UI.

## ZREALIZOWANE ELEMENTY INFRASTRUKTURY:
- [x] System nawigacji i zarządzanie sesją.
- [x] Synchronizacja plików CSV między zegarkiem a telefonem.
- [x] Material 3 UI na telefonie.
- [x] Dynamiczne formatowanie dystansu na ekranie głównym (m/km).
