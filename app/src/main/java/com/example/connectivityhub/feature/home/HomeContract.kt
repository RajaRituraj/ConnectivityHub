package com.example.connectivityhub.feature.home

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

// ═══════════════════════════════════════════════════════
//  Home — MVI Contract
// ═══════════════════════════════════════════════════════

/** All actions the user can take from the Home screen. */
sealed interface HomeIntent {
    object NavigateToWifi      : HomeIntent
    object NavigateToBluetooth : HomeIntent
    object NavigateToBle       : HomeIntent
    object RefreshStatus       : HomeIntent
}

/** The complete UI state for the Home screen. */
data class HomeState(
    val isWifiEnabled:      Boolean = false,
    val isBluetoothEnabled: Boolean = false,
    val isBleSupported:     Boolean = false,
    val connectedSsid:      String? = null,
    val connectedBtDevice:  String? = null,
    val tips:               ImmutableList<String> = persistentListOf(),
    val isLoading:          Boolean = false,
)
