package com.example.connectivityhub.feature.home

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.net.wifi.WifiManager
import androidx.lifecycle.viewModelScope
import com.example.connectivityhub.core.mvi.MviViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(private val context: Context) : MviViewModel<HomeIntent, HomeState>() {

    private val _state = MutableStateFlow(HomeState())
    override val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        refreshStatus()
    }

    override fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.RefreshStatus       -> refreshStatus()
            // Navigation intents are handled by the NavController in MainNavigation
            HomeIntent.NavigateToWifi,
            HomeIntent.NavigateToBluetooth,
            HomeIntent.NavigateToBle       -> Unit
        }
    }

    private fun refreshStatus() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as? WifiManager

            val bluetoothManager = context.applicationContext
                .getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager

            val btAdapter = bluetoothManager?.adapter

            val connectedSsid = try {
                @Suppress("DEPRECATION")
                wifiManager?.connectionInfo?.ssid
                    ?.removePrefix("\"")?.removeSuffix("\"")
                    ?.takeIf { it.isNotBlank() && it != "<unknown ssid>" }
            } catch (_: SecurityException) { null }

            _state.update {
                it.copy(
                    isLoading          = false,
                    isWifiEnabled      = wifiManager?.isWifiEnabled == true,
                    isBluetoothEnabled = btAdapter?.isEnabled == true,
                    isBleSupported     = context.packageManager.hasSystemFeature(
                        android.content.pm.PackageManager.FEATURE_BLUETOOTH_LE
                    ),
                    connectedSsid      = connectedSsid,
                    tips               = persistentListOf(
                        "Tap WiFi to scan for networks",
                        "Use Bluetooth to pair nearby devices",
                        "BLE support coming soon!",
                    ),
                )
            }
        }
    }
}
