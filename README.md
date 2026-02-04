# ParkFinder (Android 12+)

Aplikacja do zapisywania miejsca zaparkowanego auta i prowadzenia do niego na mapie.
Projekt jest gotowy „na studia”: Compose + Room (SQLite) + WorkManager + czujnik światła + latarka + Google Maps.

**Minimalna wersja Android:** 12 (API 31)  
**Package name:** `pl.edu.ur.wg131439.myapp`  
**Nazwa projektu:** `ParkFinder`

---

## ✅ Funkcje 

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





