package com.example.connectivityhub.feature.ble

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.connectivityhub.core.mvi.MviViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BleViewModel(private val context: Context) : MviViewModel<BleIntent, BleState>() {

    private val repository = BleRepository(context)

    private val _state = MutableStateFlow(BleState())
    override val state: StateFlow<BleState> = _state.asStateFlow()

    private var scanJob: Job? = null

    init {
        _state.update {
            it.copy(
                isBluetoothEnabled = repository.isEnabled,
            )
        }
    }

    override fun onIntent(intent: BleIntent) {
        when (intent) {
            BleIntent.StartScan      -> startScan()
            BleIntent.StopScan       -> stopScan()
            BleIntent.DismissError   -> _state.update { it.copy(error = null) }
            is BleIntent.ConnectDevice    -> { /* TODO: GATT connection — future sprint */ }
            is BleIntent.DisconnectDevice -> { /* TODO: GATT disconnect — future sprint */ }
        }
    }

    private fun startScan() {
        if (!repository.isBleSupported) {
            _state.update { it.copy(error = "BLE is not supported on this device") }
            return
        }
        if (!repository.isEnabled) {
            _state.update { it.copy(error = "Please enable Bluetooth to scan for BLE devices") }
            return
        }

        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _state.update { it.copy(isScanning = true, scannedDevices = kotlinx.collections.immutable.persistentListOf(), error = null) }

            val accumulated = mutableListOf<BleDeviceUiModel>()

            repository.startScan()
                .catch { e ->
                    _state.update { it.copy(isScanning = false, error = e.message) }
                }
                .collect { device ->
                    if (accumulated.none { it.address == device.address }) {
                        accumulated.add(device)
                        _state.update { it.copy(scannedDevices = accumulated.toImmutableList()) }
                    } else {
                        // Update RSSI for existing device
                        val idx = accumulated.indexOfFirst { it.address == device.address }
                        if (idx >= 0) accumulated[idx] = device
                        _state.update { it.copy(scannedDevices = accumulated.toImmutableList()) }
                    }
                }
        }
    }

    private fun stopScan() {
        scanJob?.cancel()
        _state.update { it.copy(isScanning = false) }
    }

    override fun onCleared() {
        super.onCleared()
        scanJob?.cancel()
    }
}
