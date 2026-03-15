# Instrukcje dla Agenta Projektu

## Tech Stack
- Język: Kotlin (najnowsza wersja)
- UI: Jetpack Compose for Wear OS (1.5.0+)
- Architektura: MVVM (Model-View-ViewModel) / Modularna architektura sensorów
- Komunikacja: Wear OS Data Layer API
- Przechowywanie danych: Jetpack DataStore (ustawienia), Room Database (treningi)

## Zasady Kodowania
- Zawsze używaj `ScalingLazyColumn` zamiast `Column` na zegarku.
- Obsługuj `Ambient Mode` dla każdego ekranu.
- Zawsze używaj `Ambient Mode` lub `Wakelock` podczas aktywnego treningu, aby zapobiec uśpieniu sensorów GPS/HR.
- Kolory: Ciemny motyw (AMOLED-friendly), główny akcent: Szary półprzezroczysty.
- Sensory: Używaj wzorca `remember*` z pakietu `presentation.sensors`.
- Lokalizacja: Zawsze sprawdzaj uprawnienia (Permissions) przed odpaleniem sensorów.
- Wszystkie sensory wymagające uprawnień muszą mieć obsłużony stan 'Brak uprawnień' (Permission Denied) w sposób czytelny dla użytkownika (np. ekran z prośą o włączenie w ustawieniach).

## Zarządzanie Plikami i Logami
- Baza danych: Wszystkie logi i podsumowania zapisuj w bazie danych Room.
- Optymalizacja: Dla logów w bazie danych używaj buforowania w pamięci RAM i zapisu w blokach lub przy pauzie/końcu treningu w celu oszczędzania baterii.

## Nawigacja
- Standard: `SwipeDismissableNavHost`.
- Gesty: Umożliwiaj powrót gestem swipe w prawo (nie zamykaj okien automatycznie po wyborze opcji w ustawieniach, chyba że użytkownik kliknie "Zatwierdź").
- Korona zegarka (Rotary): Każdy przewijalny ekran musi obsługiwać nawigację kółkiem fizycznym.

## Testowanie i Stabilność
- **KAŻDY** nowy feature musi posiadać odpowiadające mu testy jednostkowe (Unit Tests) lub integracyjne.
- Przed zatwierdzeniem jakichkolwiek zmian, należy upewnić się, że **WSZYSTKIE** istniejące testy przechodzą pomyślnie.
- Komenda do weryfikacji testów: `./gradlew test` lub specyficzne zadania dla modułów (np. `:mobile:testDebugUnitTest`).

## Ważne
- Dodawaj nowe funkcjonalności na końcu istniejących plików.
- Nie usuwaj komentarzy oznaczonych jako // IMPORTANT.
- Przed dodaniem nowej biblioteki zawsze aktualizuj `libs.versions.toml`.
- Zanim zmienisz plik, przeanalizuj istniejące zależności.
