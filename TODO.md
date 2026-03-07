# Lista Funkcjonalności do Zakodowania - SportApp

## 1. Integracja z sensorami (Wear OS)
- [ ] **Odczytywanie wartości z czytników**:
    - [ ] Tętno (Heart Rate) w czasie rzeczywistym.
    - [ ] Licznik kroków (Step Counter).
    - [ ] Obliczanie odległości na podstawie kroków i GPS.
    - [ ] Śledzenie trasy (GPS Location tracking).

## 2. Logowanie i dane treningowe
- [ ] **Zapisywanie treningów**:
    - [ ] Zaprojektowanie formatu pliku XML/JSON do przechowywania historii.
    - [ ] Logowanie wszystkich parametrów (czas, tętno, lokalizacja) do lokalnej bazy danych lub pliku.

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
