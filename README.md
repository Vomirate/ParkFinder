# ParkFinder (Android 12+)

Aplikacja do zapisywania miejsca zaparkowanego auta i prowadzenia do niego na mapie.
Projekt jest gotowy „na studia”: Compose + Room (SQLite) + WorkManager + czujnik światła + latarka + Google Maps.

**Minimalna wersja Android:** 12 (API 31)  
**Package name:** `pl.edu.ur.wg131439.myapp`  
**Nazwa projektu:** `ParkFinder`

---

## ✅ Funkcje (zgodnie z założeniami)

1. **Zaparkuj tutaj**  
   Zapisuje współrzędne GPS + (jeśli się uda) adres.

2. **Pokaż auto (mapa + trasa)**  
   Otwiera ekran z mapą Google, markerem auta i trasą:
   - jeśli masz włączone Directions API → dostajesz trasę „po drogach”
   - jeśli nie → aplikacja rysuje awaryjnie linię prostą (żeby zawsze działało)

3. **Przypomnienie o czasie parkowania**  
   Ustawiasz timer (30 min / 1h / 2h).  
   Po czasie dostajesz powiadomienie z akcjami:
   - **Przedłuż 15 min**
   - **Usuń**

4. **Tryb nocny / czujnik światła**  
   Gdy jest ciemno (lux < ~10), aplikacja sugeruje latarkę.

5. **Latarka**  
   Włącznik latarki w aplikacji (torch).  
   *Uwaga: emulator zwykle nie ma latarki — testuj na telefonie.*

6. **Historia parkowań (SQLite/Room)**  
   Lista **ostatnich 3** zaparkowań.

---

# 1) Jak uruchomić projekt w Android Studio (od ZIP-a)

## KROK 1 — pobierz i rozpakuj
1. Pobierz ZIP.
2. Rozpakuj np. do `D:\Projekty\ParkFinder\`.

**Ważne:** folder który otwierasz w Android Studio musi zawierać plik `settings.gradle`.

## KROK 2 — otwórz w Android Studio
1. Android Studio → **File → Open**
2. Wybierz folder `ParkFinder`
3. Poczekaj na **Gradle Sync**

Jeżeli pojawi się pytanie „Trust project?” → kliknij **Trust**.

## KROK 3 — ustaw JDK 17 (ważne!)
AGP 8.x wymaga JDK 17.

Android Studio:
- **File → Settings → Build, Execution, Deployment → Gradle**
- **Gradle JDK** → wybierz:
  - **Embedded JDK (17)** (najlepiej), albo
  - zainstalowane **JDK 17**

## KROK 4 — wklej klucz Google Maps do projektu
W folderze projektu jest plik:
- `local.properties.example`

Zrób tak:
1. Skopiuj `local.properties.example`
2. Zmień nazwę kopii na **local.properties**
3. W pliku `local.properties` wpisz:

```
MAPS_API_KEY=TU_WKLEJ_SWÓJ_KLUCZ
```

Jeżeli Android Studio dopisało tam też `sdk.dir=...` → zostaw.

✅ Teraz mapa będzie mogła działać.

## KROK 5 — uruchom aplikację
- Kliknij **Run ▶**  
- Wybierz emulator albo telefon

---

# 2) Jak odpalić na telefonie (Android 12+)

1. Telefon → Ustawienia → Informacje o telefonie → 7× kliknij **Numer kompilacji**
2. Wejdź w **Opcje programisty**
3. Włącz **Debugowanie USB**
4. Podłącz telefon kablem i zaakceptuj okno RSA
5. Android Studio → **Run ▶**

---

# 3) Google Maps: konfiguracja Google Cloud krok po kroku

## 3.1 Wybierz/utwórz projekt
1. Wejdź do **Google Cloud Console**
2. U góry wybierz projekt (np. `ParkFinder`) albo utwórz nowy

## 3.2 Podłącz Billing (wymagane przez Maps)
1. Menu **☰** → **Billing / Rozliczenia**
2. **Link billing account / Połącz konto rozliczeniowe**

To nie znaczy, że płacisz od razu — przy projekcie studenckim zwykle zostajesz na 0 zł.

## 3.3 Włącz wymagane API
Menu **☰ → APIs & Services → Library**

Włącz:
✅ **Maps SDK for Android**

Opcjonalnie (żeby trasa była „po drogach”):
✅ **Directions API**

## 3.4 Utwórz klucz API
Menu **☰ → APIs & Services → Credentials**
- **Create credentials → API key**

Skopiuj klucz.

## 3.5 Ogranicz klucz (bardzo ważne)
Kliknij swój klucz → Edit:

### A) Application restrictions
- wybierz **Android apps**
- kliknij **Add**
- wpisz:
  - **Package name:** `pl.edu.ur.wg131439.myapp`
  - **SHA-1:** (patrz punkt 4)

### B) API restrictions
- wybierz **Restrict key**
- zaznacz:
  - **Maps SDK for Android**
  - (opcjonalnie) **Directions API**

Zapisz.

---

# 4) Skąd wziąć SHA‑1? (2 pewne metody)

## METODA A — Android Studio (signingReport)
1. Android Studio → po prawej zakładka **Gradle**
2. Otwórz:
   `ParkFinder → Tasks → android → signingReport`
3. Kliknij `signingReport`
4. W wynikach szukasz:

```
Variant: debug
SHA1: AA:BB:CC:...
```

To kopiujesz do Google Cloud.

## METODA B — keytool (działa zawsze)
Windows PowerShell:

```powershell
keytool -list -v -alias androiddebugkey -keystore "$env:USERPROFILE\.android\debug.keystore" -storepass android -keypass android
```

Szukasz linii `SHA1:` i kopiujesz.

---

# 5) Najczęstsze problemy i naprawy

## „Gradle project sync failed”
1. Kliknij **Open "Build" View** i sprawdź błąd.
2. Sprawdź JDK 17:
   **Settings → Gradle → Gradle JDK → Embedded JDK 17**
3. Sprawdź tryb offline:
   **Settings → Gradle → Offline work** (ma być WYŁ.)

## Mapa jest szara / pusta
- nie podłączony billing
- nie włączone `Maps SDK for Android`
- niepoprawny klucz w `local.properties`
- klucz nie ma SHA‑1 lub ma zły package name

## Trasa „po drogach” nie działa
- nie włączone `Directions API`
- ale aplikacja i tak pokaże linię prostą (fallback)

## Latarka nie działa
- emulator nie ma latarki → testuj na telefonie
- brak uprawnienia CAMERA

---

# 6) Struktura projektu (dla prowadzącego)

- Room / SQLite:
  - `data/db/*`
  - `data/repo/ParkingRepository.kt`
- GPS:
  - `location/LocationClient.kt`
- Geocoder:
  - `location/ReverseGeocoder.kt`
- Czujnik światła:
  - `sensors/LightSensorManager.kt`
- Latarka:
  - `sensors/TorchController.kt`
- Powiadomienia + WorkManager:
  - `notifications/ParkingReminderWorker.kt`
  - `notifications/ReminderActionsReceiver.kt`
  - `notifications/NotificationHelper.kt`
- UI (Compose):
  - `ui/screens/HomeScreen.kt`
  - `ui/screens/MapScreen.kt`
  - `ui/screens/HistoryScreen.kt`

---

Powodzenia ✌️
