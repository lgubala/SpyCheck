# SpyCheck - Privacy Awareness Demo App

SpyCheck is an educational Android application that demonstrates how apps can track users and collect personal data, often without explicit permissions. The app showcases various tracking techniques through interactive demos, helping users understand modern privacy threats.

## Download

[![Download APK](https://img.shields.io/badge/Download-APK-brightgreen?style=for-the-badge&logo=android)](https://github.com/lgubala/SpyCheck/releases/latest/download/SpyCheck-v1.1.apk)

> **Note:** Android will show a warning about installing apps from unknown sources — this is expected for apps not distributed via Google Play. You may also see a Play Protect warning since this APK hasn't built up reputation yet; this is normal for open-source apps distributed on GitHub.

See all releases on the [Releases page](https://github.com/lgubala/SpyCheck/releases).

**SHA-256 checksum:** `DD6EAF0ACBB892F9E2C1F462EF13C1D3FAB8E364DF55DEE0A8F8198C44EDAE3F`

To verify the download on Windows (PowerShell):
```powershell
Get-FileHash "SpyCheck-v1.1.apk" -Algorithm SHA256
```

To verify on Linux/macOS:
```bash
sha256sum SpyCheck-v1.1.apk
```


## Features

- **Real-Time Tracking Demos**: See how apps monitor your activity in real-time
- **Device Fingerprinting**: Learn how unique device characteristics can identify you
- **Sneaky Data Collection**: Discover hidden tracking methods that require minimal permissions
- **Interactive Examples**: Hands-on demonstrations of GPS extraction, WiFi location tracking, clipboard snooping, and more

## Requirements

- Android 6.0 (API 23) or higher
- Google Maps API key (for location-based demos)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone https://github.com/lgubala/spycheck.git
cd spycheck
```

### 2. Get a Google Maps API Key

To enable WiFi location tracking and other map-based features, you'll need a Google Maps API key:

1. Go to the [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project (or select an existing one)
3. Enable the following APIs:
   - **Maps SDK for Android**
   - **Geolocation API**
4. Navigate to **Credentials** in the sidebar
5. Click **Create Credentials** → **API Key**
6. Copy your API key
7. (Recommended) Click **Restrict Key** and limit it to:
   - Android apps (add your app's package name and SHA-1 certificate fingerprint)
   - Specific APIs (Maps SDK for Android, Geolocation API)

### 3. Add API Key to Project

Create or edit the `local.properties` file in your project root directory and add your API key:

```properties
MAPS_API_KEY=AIzaSyC_your_actual_api_key_here
```

**Important**: Never commit your `local.properties` file to version control. It's already included in `.gitignore`.

### 4. Build and Run

Open the project in Android Studio and run it on a device or emulator.

## Privacy Note

This app is designed for **educational purposes only**. It demonstrates privacy risks to raise awareness, not to facilitate actual surveillance. All demos require explicit user permission and clearly explain what data is being accessed.

## Permissions

The app requests various permissions to demonstrate different tracking techniques. These include:

- **Photos**: To show GPS data in EXIF metadata
- **Location**: For WiFi-based location tracking demos
- **Activity Recognition**: For sensor fingerprinting (Android 10+)
- **Audio Recording**: For audio fingerprinting demos

**Always revoke permissions after testing demos**, as the app itself explains.


## Disclaimer

This application is intended solely for educational purposes to demonstrate privacy vulnerabilities in modern mobile applications. Users should be aware of their local laws regarding data collection and privacy before using this app.
