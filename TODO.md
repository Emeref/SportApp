# Lista Funkcjonalności do Zakodowania - SportApp

## 1. Integracja z sensorami (Wear OS)
- [x] **Odczytywanie wartości z czytników**:
    - [x] Tętno (Heart Rate) w czasie rzeczywistym.
    - [x] Licznik kroków (Step Counter).
    - [x] Obliczanie odległości na podstawie kroków i GPS.
    - [x] Wyliczanie spalonych kalorii (Modele: Keytel, MET, HRR).
    - [ ] Śledzenie trasy (GPS Location tracking - wizualizacja na mapie już jest, brak zapisu ścieżki).

## 2. Zarządzanie danymi i plikami (OBECNY BRANCH)
- [x] **Zapis konfiguracji aplikacji**:
    - [x] Automatyczne zapisywanie i wczytywanie ustawień (mapa, zegar, dane zdrowotne) do pliku JSON.
- [ ] **Konfiguracja aktywności (XML)**:
    - [ ] Stworzenie parsera XML dla układu sensorów (pozycja wiersz_kolumna).
- [ ] **Logowanie treningów (CSV)**:
    - [ ] Rejestracja parametrów co sekundę do pliku dedykowanego dla sesji.
    - [ ] Obliczanie średniego BPM, przewyższeń (góra/dół) i dystansu z kroków w locie.
- [ ] **Podsumowanie zbiorcze (CSV)**:
    - [ ] Dopisywanie wyników zakończonej sesji do pliku `Podsumowanie_cwiczen.csv`.

## 3. Komunikacja międzyurządzeniowa
- [ ] **Aplikacja na telefon (Mobile App)**:
    - [ ] Stworzenie interfejsu mobilnego.
    - [ ] Wykorzystanie `Wearable Data Layer API` do łączenia się z zegarkiem.
    - [ ] Synchronizacja danych treningowych z zegarka na telefon po zakończeniu sesji.

## 4. Chmura i Bezpieczeństwo
- [ ] **Integracja z Google**:
    - [ ] Dodanie logowania przez konto Google (Google Sign-In).
    - [ ] Automatyczny backup danych treningowych na koncie Google (np. Google Drive App Data lub Firebase).

## 5. Jakość i Testy
- [ ] **Unit testy**:
    - [ ] Testy jednostkowe logiki biznesowej (obliczanie dystansu, formatowanie danych).
    - [ ] Testy komponentów UI (Compose Previews i screenshot tests).
    - [ ] Testy integracyjne nawigacji.

## ZREALIZOWANE ELEMENTY INFRASTRUKTURY:
- [x] System nawigacji (SwipeDismissableNavHost).
- [x] Zarządzanie sesją (Pauza/Zakończ).
- [x] Ustawienia danych zdrowotnych (Wiek, Waga, Wzrost, Płeć, HR).
- [x] Dynamiczna mapa (Google Maps) z wyborem typu w opcjach.
- [x] Modularna architektura sensorów.
