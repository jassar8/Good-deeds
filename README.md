# Prayer Reminder App

A simple Android app (frontend only) that reminds Muslim users to pray on time based on their location.

## 📱 Download the App (APK)

[✅ **Download app-debug.apk**](https://example.com/download/app-debug.apk)

If the APK doesn't install, enable **"Install Unknown Apps"** on your Android device.

## Features

- **Main Screen**: Displays current location (dummy) and 5 daily prayer times with toggle switches to enable/disable reminders
- **Settings Screen**: Theme selection (Light/Dark), calculation method selection, and location permission status
- **Material Design 3**: Modern, clean UI with Material Design 3 components
- **Jetpack Navigation**: Bottom navigation between Home and Settings screens

## Project Structure

```
app/src/main/
├── java/com/prayerreminder/app/
│   ├── ui/
│   │   ├── main/
│   │   │   └── MainActivity.kt
│   │   ├── home/
│   │   │   ├── HomeFragment.kt
│   │   │   ├── adapter/
│   │   │   │   └── PrayerAdapter.kt
│   │   │   └── model/
│   │   │       └── Prayer.kt
│   │   └── settings/
│   │       └── SettingsFragment.kt
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   ├── fragment_home.xml
│   │   ├── fragment_settings.xml
│   │   └── item_prayer.xml
│   ├── navigation/
│   │   └── mobile_navigation.xml
│   ├── menu/
│   │   └── bottom_nav_menu.xml
│   └── values/
│       ├── strings.xml
│       ├── colors.xml
│       └── themes.xml
└── AndroidManifest.xml
```

## Building the APK

### Prerequisites

- Android SDK (API 24 or higher)
- JDK 17 or higher
- Gradle 8.2+ (or use Android Studio which includes it)

### Important Note: Gradle Wrapper JAR

The Gradle wrapper scripts (`gradlew.bat` on Windows, `gradlew` on Unix) will automatically download the `gradle-wrapper.jar` file if it's missing. This happens automatically on first run.

If automatic download fails, you can manually download it using:
- **Windows**: Run `powershell -ExecutionPolicy Bypass -File download-wrapper.ps1`
- **Unix/Mac**: The `gradlew` script will attempt to download it automatically using `curl` or `wget`

Alternatively, you can use Android Studio which will automatically handle the Gradle wrapper setup.

### Build Commands

**On Windows (PowerShell or Command Prompt):**
```bash
.\gradlew.bat assembleDebug
```

**On macOS/Linux:**
```bash
./gradlew assembleDebug
```

### APK Location

After building, the debug APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Running on Your Android Phone

### Option 1: Using ADB (Android Debug Bridge)

1. **Enable Developer Options** on your Android device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
   - Developer options will appear in Settings

2. **Enable USB Debugging**:
   - Go to Settings → Developer Options
   - Enable "USB Debugging"

3. **Connect your phone** via USB cable

4. **Verify connection**:
   ```bash
   adb devices
   ```
   You should see your device listed

5. **Install the APK**:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### Option 2: Direct Transfer

1. Build the APK using the commands above
2. Copy `app/build/outputs/apk/debug/app-debug.apk` to your phone
3. On your phone, open the APK file
4. Allow installation from unknown sources if prompted
5. Install the app

### Option 3: Using Android Studio

1. Open the project in Android Studio
2. Click "Run" or press `Shift+F10`
3. Select your connected device or emulator
4. The app will build and install automatically

## Technical Details

- **Language**: Kotlin
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **UI Framework**: Material Design 3
- **Navigation**: Jetpack Navigation Component
- **Architecture**: MVVM with Repository, Room database, Retrofit API client, and Fragment-based UI with ViewBinding

## Backend / Data Layer

The app now includes a full backend/data layer behind the existing UI:

- **API configuration**:
  - Uses a Retrofit client (`PrayerApiService`) pointed at the AlAdhan prayer times API (`https://api.aladhan.com/`).
  - Base URL is configured in `ApiClientProvider`.
  - Network responses are mapped into domain models via `PrayerApiMapper`.
- **Room database**:
  - `PrayerAppDatabase` defines:
    - `PrayerTimesEntity` for today’s prayer times.
    - `ReminderSettingsEntity` for per-prayer reminder toggles and offsets.
    - `SettingsEntity` for calculation method, theme, and last known location.
  - DAOs expose both suspend functions and `Flow` streams to drive the UI reactively.
- **Repository**:
  - `PrayerRepositoryImpl` coordinates:
    - Current location (via `LocationProvider` and `GeocodingService`).
    - API calls and caching in Room.
    - Reminder settings and app settings.
    - Next-prayer calculations via `PrayerTimeCalculator`.
- **ViewModels**:
  - `HomeViewModel` exposes a `StateFlow<HomeUiState>` with:
    - Loading/error flags.
    - City name, today’s prayer times, next-prayer countdown, and reminder settings.
  - `SettingsViewModel` exposes `StateFlow<AppSettings>` for calculation method and theme.

## Location and Permissions

- The app uses Google Play Services `FusedLocationProviderClient` via `LocationProvider`.
- `HomeFragment` requests fine location permission at runtime; when denied, a default fallback location from `LocationDefaults` is used.
- `GeocodingService` uses Android `Geocoder` on a background dispatcher to resolve a human-readable city name.

## Reminders

- A `ReminderScheduler` interface is defined in the domain layer with a stub implementation `ReminderSchedulerStub` that only logs scheduling calls for now.
- Real notifications (AlarmManager/WorkManager) can be implemented later by providing a concrete `ReminderScheduler` implementation without changing ViewModels or Repository.

## Testing the Backend

1. **Run the app** on a device or emulator with network connectivity.
2. **Grant location permission** when prompted on the Home screen.
3. Verify that:
   - City name updates from your current or fallback location.
   - Prayer list times update from the API (and persist between launches via Room).
   - The header countdown updates every second towards the next prayer.
   - Toggling a prayer row updates reminder settings and is preserved across restarts.
4. In Settings:
   - Toggle the calculation method; this triggers a refresh of cached prayer times.
   - Change the theme toggle (UI only, but stored in `AppSettings`).
