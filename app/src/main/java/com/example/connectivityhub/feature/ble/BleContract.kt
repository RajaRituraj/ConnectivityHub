package com.example.connectivityhub.feature.ble

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// ═══════════════════════════════════════════════════════
//  BLE — MVI Contract (Scaffolded for future implementation)
// ═══════════════════════════════════════════════════════

/** UI model for a BLE peripheral device. */
data class BleDeviceUiModel(
    val name:        String,
    val address:     String,     // MAC address
    val rssi:        Int = 0,    // Signal strength in dBm
    val isConnected: Boolean = false,
    val services:    ImmutableList<String> = persistentListOf(), // Service UUIDs
) {
    val displayName: String get() = name.ifBlank { "Unknown BLE Device" }
}

/** Connection state for a BLE peripheral. */
enum class BleConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
}

/** All BLE actions (to be fully implemented in a future sprint). */
sealed interface BleIntent {
    object StartScan                                          : BleIntent
    object StopScan                                          : BleIntent
    data class ConnectDevice(val device: BleDeviceUiModel)   : BleIntent
    data class DisconnectDevice(val device: BleDeviceUiModel): BleIntent
    object DismissError                                      : BleIntent
}

/** Complete UI state for the BLE screen. */
data class BleState(
    val isLoading:          Boolean = false,
    val isBluetoothEnabled: Boolean = false,
    val isScanning:         Boolean = false,
    val scannedDevices:     ImmutableList<BleDeviceUiModel> = persistentListOf(),
    val connectedDevice:    BleDeviceUiModel? = null,
    val connectionState:    BleConnectionState = BleConnectionState.DISCONNECTED,
    val error:              String? = null,
)
