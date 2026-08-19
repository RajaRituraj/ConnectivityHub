# 📡 ConnectivityHub

A modern Android connectivity toolkit demonstrating **Wi-Fi, Bluetooth, and Bluetooth Low Energy (BLE)** using **Kotlin, Jetpack Compose, and MVI architecture**.

The project focuses on practical Android connectivity APIs, runtime permissions, device discovery, connection state management, and reactive UI updates.

## ✨ Features

* 📶 Wi-Fi network scanning and connectivity status
* 🔵 Bluetooth device discovery
* 🟦 Bluetooth Low Energy (BLE) scanning
* 🔗 Device connection state handling
* 🔐 Runtime permission handling
* ⚡ Reactive UI using Jetpack Compose
* 🧠 MVI-based state management
* 🔄 Kotlin Coroutines and Flow
* 🧩 Clean and reusable components

## 🛠 Tech Stack

* **Kotlin**
* **Jetpack Compose**
* **MVI Architecture**
* **Coroutines**
* **Flow / StateFlow**
* **Android Connectivity APIs**
* **Bluetooth / BLE APIs**
* **Wi-Fi APIs**
* **Gradle Kotlin DSL**

## 🏗 Architecture

The application follows an MVI-style unidirectional data-flow approach:

```text
User Action
    ↓
Intent
    ↓
ViewModel
    ↓
Repository / Connectivity Layer
    ↓
Android Connectivity APIs
    ↓
Result / State
    ↓
StateFlow
    ↓
Jetpack Compose UI
```

This keeps UI state predictable and separates connectivity logic from presentation.

## 📱 Connectivity Modules

### Wi-Fi

Demonstrates Android Wi-Fi APIs for:

* Network discovery
* Current connection information
* Connectivity state
* Permission handling

### Bluetooth

Demonstrates:

* Bluetooth availability
* Adapter state
* Device discovery
* Pairing/device information
* Connection state

### BLE

Demonstrates:

* BLE scanning
* Device discovery
* Scan result handling
* Connection workflow

## 🔐 Permissions

The application handles Android runtime permissions required by modern Android versions for Bluetooth and Wi-Fi operations.

Permission behavior may vary depending on Android version and device manufacturer.

## 📂 Project Structure

```text
app/
├── data/
│   ├── bluetooth/
│   ├── ble/
│   └── wifi/
│
├── domain/
│   └── models/
│
├── presentation/
│   ├── bluetooth/
│   ├── ble/
│   ├── wifi/
│   └── components/
│
└── MainActivity.kt
```

## 🚀 Getting Started

### Requirements

* Android Studio
* JDK 17+
* Android SDK
* Physical Android device recommended for Bluetooth/BLE testing

### Run

```bash
git clone https://github.com/RajaRituraj/ConnectivityHub.git
cd ConnectivityHub
```

Open the project in Android Studio and run it on a physical Android device.

> Bluetooth and BLE functionality may not work correctly on an emulator because these features depend on device hardware.

## 🧪 Testing

The project can be extended with:

* ViewModel unit tests
* Repository tests
* Permission-flow tests
* UI tests using Compose Testing APIs

## 🎯 What This Project Demonstrates

This project showcases practical Android development skills including:

* Android framework APIs
* Modern Kotlin development
* Jetpack Compose
* MVI and unidirectional data flow
* Reactive state management
* Coroutines and Flow
* Runtime permissions
* Bluetooth/BLE development
* Wi-Fi connectivity
* Separation of concerns

## 🔮 Future Improvements

* Add automated unit and UI tests
* Add CI using GitHub Actions
* Add device connection history
* Add BLE GATT service/characteristic discovery
* Add network diagnostics
* Add connection retry and timeout handling
* Add screenshots and demo video

## 👨‍💻 Author

**Raja Rituraj**

Android Developer with 4+ years of experience building production applications using Kotlin, Jetpack Compose, MVVM/MVI, Clean Architecture, Coroutines, Flow, REST APIs, and modern Android technologies.

* GitHub: https://github.com/RajaRituraj
* LinkedIn: https://www.linkedin.com/in/raja-rituraj-600468221/

---

⭐ If you find this project useful, consider giving it a star.

