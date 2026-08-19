# 📡 ConnectivityHub

A modern Android sample app for exploring **Wi-Fi, Bluetooth, and Bluetooth Low Energy (BLE)** using **Kotlin, Jetpack Compose, Coroutines, Flow, and MVI-style state management**.

The project is designed as a practical reference for Android connectivity APIs, runtime permissions, device discovery, connection state, and reactive UI.

## ✨ Features

- 📶 Wi-Fi connectivity and network information
- 🔵 Classic Bluetooth discovery and connection state
- 🟦 Bluetooth Low Energy (BLE) scanning
- 🔐 Runtime permission handling for modern Android versions
- ⚡ Jetpack Compose UI
- 🧠 Contract + ViewModel based state management
- 🔄 Kotlin Coroutines and Flow
- 🧩 Feature-oriented package structure
- 🧭 Compose Navigation

## 🛠 Tech Stack

| Technology | Usage |
|---|---|
| Kotlin | Application development |
| Jetpack Compose | Declarative UI |
| Material 3 | UI components |
| Coroutines | Asynchronous operations |
| Flow / StateFlow | Reactive state |
| ViewModel | Presentation state |
| Navigation | Screen navigation |
| Android Bluetooth APIs | Bluetooth discovery/connectivity |
| Android BLE APIs | BLE scanning |
| Android Wi-Fi APIs | Wi-Fi connectivity |
| Accompanist Permissions | Runtime permission handling |
| Kotlin Serialization | Serialization support |

## 🏗 Architecture

The project follows a feature-oriented architecture with a clear separation between UI, state management, and connectivity/data logic.

```text
                    Jetpack Compose UI
                           │
                           ▼
                       ViewModel
                           │
                           ▼
                       Contract
                    (Intent / State)
                           │
                           ▼
                       Repository
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
          Bluetooth       BLE          Wi-Fi
             APIs         APIs          APIs
```

Each connectivity feature is isolated under its own package, making the code easier to understand, test, and extend.

## 📂 Project Structure

```text
app/src/main/java/com/example/connectivityhub/
├── core/
├── feature/
│   ├── ble/
│   ├── bluetooth/
│   ├── home/
│   └── wifi/
├── theme/
├── MainActivity.kt
└── Navigation.kt
```

For example, the Bluetooth feature contains a contract, repository, ViewModel, and UI layer:

```text
feature/bluetooth/
├── BluetoothContract.kt
├── BluetoothRepository.kt
├── BluetoothViewModel.kt
└── ui/
```

The BLE and Wi-Fi features follow the same feature-oriented approach.

## 📱 Connectivity Areas

### Bluetooth

- Bluetooth availability/state
- Device discovery
- Device information
- Connection-state handling

### Bluetooth Low Energy (BLE)

- BLE device scanning
- Scan result handling
- Device discovery
- BLE connection workflow

### Wi-Fi

- Wi-Fi connectivity information
- Network-related state
- Runtime permission handling

## 🔐 Android Permissions

Connectivity APIs have different permission requirements across Android versions. This project demonstrates runtime permission handling for Bluetooth and Wi-Fi operations.

For the most reliable Bluetooth/BLE testing, use a **physical Android device** with Bluetooth hardware enabled.

## ⚙️ Build Configuration

- **compileSdk:** 36
- **targetSdk:** 36
- **minSdk:** 24
- **Java:** 17
- **Kotlin JVM Toolchain:** 17
- **UI:** Jetpack Compose

## 🚀 Getting Started

### Requirements

- Android Studio
- JDK 17+
- Android SDK 36
- Physical Android device recommended for Bluetooth/BLE testing

### Clone

```bash
git clone https://github.com/RajaRituraj/ConnectivityHub.git
cd ConnectivityHub
```

Open the project in Android Studio, allow Gradle synchronization to finish, then run the `app` configuration.

## 🧪 Testing

The project is configured with dependencies for both local JVM tests and Compose/instrumented Android tests.

Recommended coverage includes:

- ViewModel state transitions
- Repository behavior
- Permission-related states
- Compose UI interactions
- Connectivity error states

## 🎯 What This Project Demonstrates

This project is intended to demonstrate practical Android engineering skills rather than a simple UI demo:

- Modern Kotlin development
- Jetpack Compose and Material 3
- MVI-style unidirectional state management
- ViewModel + StateFlow
- Coroutines and asynchronous Android APIs
- Bluetooth and BLE development
- Wi-Fi APIs
- Runtime permission handling
- Feature-oriented architecture
- Repository pattern
- Android navigation

## 🔮 Planned Improvements

- Add comprehensive unit tests for connectivity state handling
- Add Compose UI tests
- Add GitHub Actions CI for build and test verification
- Add screenshots and a short demo video
- Add BLE GATT service/characteristic discovery
- Add connection retry and timeout handling
- Add richer network diagnostics

## 👨‍💻 Author

**Raja Rituraj**  
Android Developer | Kotlin | Jetpack Compose | MVI | Clean Architecture

4+ years of professional Android development experience across production applications in logistics, healthcare, e-commerce, and education.

- GitHub: https://github.com/RajaRituraj
- LinkedIn: https://www.linkedin.com/in/raja-rituraj-600468221/

---

⭐ If you find this project useful, consider giving it a star.
