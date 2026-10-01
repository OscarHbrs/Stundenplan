# Stundenplan H-BRS

Stundenplan-App für Erstis an der H-BRS (BCSP, BI, BWI) – für **iPhone, Android und Browser**.

## Installieren

### iPhone / iPad
1. **Safari** öffnen: <https://oscarhbrs.github.io/Stundenplan/>
2. Unten auf **Teilen** tippen → **Zum Home-Bildschirm**.
3. Die App startet dann wie eine normale App im Vollbild und funktioniert nach dem ersten Öffnen auch offline.

Voraussetzung: iOS 18.2 oder neuer.

### Android
- **App:** Die neueste `Stundenplan-….apk` unter [Releases](https://github.com/OscarHbrs/Stundenplan/releases/latest) herunterladen und öffnen. Beim ersten Mal muss die Installation aus unbekannten Quellen erlaubt werden.
- **oder Web-App:** <https://oscarhbrs.github.io/Stundenplan/> in Chrome öffnen → Menü → **App installieren**.

## Projektstruktur

Kotlin Multiplatform + Compose Multiplatform – die Oberfläche wird einmal geschrieben und läuft auf allen Plattformen.

| Modul        | Inhalt                                                                  |
|--------------|-------------------------------------------------------------------------|
| `shared/`    | Gesamte App: Stundenplandaten (`data/`) und Oberfläche (`ui/`)          |
| `androidApp/`| Android-Einstieg (`MainActivity`) und Speicherung per SharedPreferences |
| `webApp/`    | Web-Einstieg (Kotlin/Wasm), `index.html`, PWA-Manifest, Service Worker  |
| `mensa/`     | Mensa-Speiseplan: Datenmodell, HTML-Parser und JSON-Export (`jvmMain`)  |

Stundenplan ändern: `shared/src/commonMain/kotlin/io/github/oscarhbrs/stundenplan/data/ScheduleData.kt`.

## Mensa-Speiseplan

Der Speiseplan der Mensa Sankt Augustin kommt live von der Webseite des Studierendenwerks Bonn.

- **Android** lädt die Seite direkt und speichert den letzten Stand für offline.
- **Web/iPhone:** Der Browser darf die Seite des Studierendenwerks nicht direkt laden (CORS). Darum erzeugt
  der Workflow `deploy-web.yml` mehrmals täglich eine `mensa.json` und veröffentlicht sie mit der Web-App.

Lokal: `./gradlew :mensa:jvmRun --args=$PWD/mensa.json` schreibt den aktuellen Plan, `./gradlew :mensa:jvmTest`
prüft den Parser gegen gespeicherte Antworten der Webseite.

**Wichtig:** GitHub pausiert geplante Workflows, wenn 60 Tage lang nichts ins Repository gepusht wurde. Dann
unter *Actions* → *Web-App deployen* → **Enable workflow** wieder einschalten, sonst bleibt der Speiseplan
der Web-App auf dem alten Stand.

## Entwickeln

```sh
./gradlew :androidApp:installDebug              # Android-App aufs Gerät/Emulator
./gradlew :webApp:wasmJsBrowserDevelopmentRun   # Web-App lokal im Browser starten
./gradlew :webApp:wasmJsBrowserDistribution     # Web-App bauen → webApp/build/dist/wasmJs/productionExecutable
```

Die App-Icons der Web-App werden aus `webApp/icon.svg` erzeugt:

```sh
cd webApp && for s in 180:apple-touch-icon 192:icon-192 512:icon-512 512:icon-maskable-512; do
  rsvg-convert -w ${s%%:*} -h ${s%%:*} icon.svg -o src/wasmJsMain/resources/icons/${s#*:}.png
done
```

## Veröffentlichen (GitHub Actions)

- **Web-App:** Jeder Push auf `main` baut die Web-App und veröffentlicht sie auf GitHub Pages
  (`.github/workflows/deploy-web.yml`). Zusätzlich läuft der Workflow zeitgesteuert (Mo–Sa alle 3 Stunden
  tagsüber und täglich um Mitternacht), um den Mensa-Speiseplan zu aktualisieren.
- **Android-APK:** Jeder Tag `v*` (z. B. `git tag v1.1.0 && git push origin v1.1.0`) baut eine
  signierte APK und hängt sie an ein GitHub-Release (`.github/workflows/release-android.yml`).

### Einmalige Einrichtung

1. **GitHub Pages:** Repository → *Settings* → *Pages* → *Source*: **GitHub Actions**.
2. **Signaturschlüssel für Android** einmalig erzeugen und **sicher aufbewahren** (ohne ihn können
   keine Updates mehr veröffentlicht werden, die sich über bestehende Installationen installieren lassen):
   ```sh
   keytool -genkeypair -v -keystore stundenplan-release.jks -alias stundenplan \
     -keyalg RSA -keysize 4096 -validity 10000
   base64 -w0 stundenplan-release.jks   # Ausgabe für ANDROID_KEYSTORE_BASE64
   ```
3. Unter *Settings* → *Secrets and variables* → *Actions* diese Secrets anlegen:
   `ANDROID_KEYSTORE_BASE64`, `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS` (`stundenplan`),
   `ANDROID_KEY_PASSWORD`.

Die `.jks`-Datei niemals committen.
