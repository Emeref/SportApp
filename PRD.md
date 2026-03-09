# Projekt: SportApp

## Wizja Biznesowa
Aplikacja ma być minimalistycznym asystentem sportowym, który gromadzi dane z wielu sensorów w trakcie treningu i zapisuje je w sposób pozwalajacy na ich latwy odczyt pozniej na komorce i ich dokladniejsze analizy.

## Cele (MVP)
1. **Rozpoczęcie aktywności sportowej**: Możliwość startu, pauzy i zakończenia treningu z poziomu zegarka.
2. **Zapis precyzyjnych danych**: Logowanie parametrów co sekundę do formatu CSV przyjaznego dla analizy (puste pola zamiast null, optymalizacja wagi pliku).
3. **Wizualizacja danych historycznych**: Przeglądanie statystyk z poprzednich treningów w czytelnej formie.
4. **Ekosystem Mobilny**: Aplikacja mobilna połączona i synchronizująca się z aplikacją WearOS za pomocą Data Layer API.
5. **Personalizacja aktywności**: Możliwość indywidualnego tworzenia swoich treningów i wyboru dowolnych dostępnych sensorów (konfiguracja oparta na XML).

## Kluczowe Funkcjonalności
- Odczyt tętna (HR), kroków, odległości (GPS/Kroki), prędkości i wysokości.
- Model obliczania spalonych kalorii: HRR (Heart Rate Reserve).
- Dynamiczna mapa z wyborem typu (Satelitarna, Hybrydowa, Normalna, Terenowa).
- System profili zdrowotnych (Płeć, Wiek, Masa, Wzrost, Tętno spoczynkowe/maksymalne).
- Nawigacja kółkiem fizycznym (Rotary Input) i gestami.

## Standardy Danych i Formatowania
- **Odległość**: Wszystkie odległości w plikach aktywności są zapisywane w **metrach**, zaokrąglone do pełnych wartości (brak centymetrów).
- **Czas**: Wszystkie czasy aktywności są zapisywane z dokładnością do **sekund**.
- **Kalorie**: Wszystkie wartości kcal są zapisywane z dokładnością do **1 miejsca po przecinku**.

## Sukces Projektu
Użytkownik może odbyć 30-minutowy spacer bez wyciągania telefonu z kieszeni, mając pełny wgląd w trasę i wartości wszystkich czujników w trakcie spaceru, a po powrocie dane automatycznie czekają na niego w telefonie gotowe do analizy.
