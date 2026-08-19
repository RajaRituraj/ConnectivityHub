package com.example.connectivityhub.feature.bluetooth

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// ═══════════════════════════════════════════════════════
//  Bluetooth — MVI Contract
// ═══════════════════════════════════════════════════════

/** Device class categories for icon display. */
enum class BluetoothDeviceCategory {
    AUDIO,    // Headphones, speakers
    PHONE,    // Smartphones
    COMPUTER, // Laptops, desktops
    WEARABLE, // Smartwatches, fitness bands
    UNKNOWN,
}

/** UI model for a single Bluetooth device. */
data class BluetoothDeviceUiModel(
    val name:     String,
    val address:  String,     // MAC address
    val rssi:     Int = 0,    // Signal strength in dBm
    val isPaired: Boolean = false,
    val isConnected: Boolean = false,
    val deviceClass: Int = 0, // BluetoothClass.Device value
) {
    val category: BluetoothDeviceCategory get() = when {
        deviceClass in 0x0400..0x04FF -> BluetoothDeviceCategory.AUDIO
        deviceClass in 0x0200..0x02FF -> BluetoothDeviceCategory.PHONE
        deviceClass in 0x0100..0x01FF -> BluetoothDeviceCategory.COMPUTER
        deviceClass in 0x0700..0x07FF -> BluetoothDeviceCategory.WEARABLE
        else                          -> BluetoothDeviceCategory.UNKNOWN
    }

    val displayName: String get() = name.ifBlank { "Unknown Device" }
}

/** All actions the user can take on the Bluetooth screen. */
sealed interface BluetoothIntent {
    object StartDiscovery                                           : BluetoothIntent
    object StopDiscovery                                           : BluetoothIntent
    object ToggleBluetooth                                         : BluetoothIntent
    data class ConnectDevice(val device: BluetoothDeviceUiModel)   : BluetoothIntent
    data class UnpairDevice(val device: BluetoothDeviceUiModel)    : BluetoothIntent
    object DismissError                                            : BluetoothIntent
    object LoadPairedDevices                                       : BluetoothIntent
}

/** Complete UI state for the Bluetooth screen. */
data class BluetoothState(
    val isLoading:         Boolean = false,
    val isBluetoothEnabled: Boolean = false,
    val isDiscovering:     Boolean = false,
    val discoveredDevices: ImmutableList<BluetoothDeviceUiModel> = persistentListOf(),
    val pairedDevices:     ImmutableList<BluetoothDeviceUiModel> = persistentListOf(),
    val connectedDevice:   BluetoothDeviceUiModel? = null,
    val error:             String? = null,
)
