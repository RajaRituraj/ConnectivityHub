package com.example.connectivityhub.feature.wifi

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// ═══════════════════════════════════════════════════════
//  WiFi — MVI Contract
// ═══════════════════════════════════════════════════════

/** UI model for a single WiFi network. */
data class WifiNetworkUiModel(
    val ssid:        String,
    val bssid:       String,
    val rssi:        Int,          // Signal strength in dBm (e.g., -50 = excellent)
    val isSecured:   Boolean,
    val isConnected: Boolean = false,
    val frequency:   Int = 0,      // MHz — 2400 or 5000
    val capabilities: String = "", // Raw capability string
) {
    /** Signal level 0–4 for display */
    val signalLevel: Int get() = when {
        rssi >= -50 -> 4
        rssi >= -60 -> 3
        rssi >= -70 -> 2
        rssi >= -80 -> 1
        else        -> 0
    }

    val frequencyBand: String get() = if (frequency > 4900) "5 GHz" else "2.4 GHz"
}

/** All actions the user can take on the WiFi screen. */
sealed interface WifiIntent {
    object ScanNetworks                                          : WifiIntent
    object ToggleWifi                                           : WifiIntent
    data class ConnectToNetwork(val ssid: String, val password: String) : WifiIntent
    object DisconnectNetwork                                    : WifiIntent
    object DismissError                                         : WifiIntent
}

/** Complete UI state for the WiFi screen. */
data class WifiState(
    val isLoading:          Boolean = false,
    val isWifiEnabled:      Boolean = false,
    val isScanning:         Boolean = false,
    val networks:           ImmutableList<WifiNetworkUiModel> = persistentListOf(),
    val connectedSsid:      String? = null,
    val connectedIpAddress: String? = null,
    val error:              String? = null,
)
