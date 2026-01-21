# Prayer Reminder App

A simple Android app (frontend only) that reminds Muslim users to pray on time based on their location.

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
- **Architecture**: Fragment-based with ViewBinding

## Notes

- This is a **frontend-only** implementation
- Prayer times are **hardcoded** (dummy data)
- Location is **dummy text** (no real location services)
- No notifications implemented yet
- Settings are UI placeholders only

## Future Enhancements

- Real location services integration
- Prayer time calculation API integration
- Notification system for prayer reminders
- Persistent storage for user preferences
- Multiple calculation methods support
