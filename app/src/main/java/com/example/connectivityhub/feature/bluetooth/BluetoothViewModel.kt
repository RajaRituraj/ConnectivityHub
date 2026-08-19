package com.example.connectivityhub.feature.bluetooth

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.example.connectivityhub.core.mvi.MviViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BluetoothViewModel(private val context: Context) : MviViewModel<BluetoothIntent, BluetoothState>() {

    private val repository = BluetoothRepository(context)

    private val _state = MutableStateFlow(BluetoothState())
    override val state: StateFlow<BluetoothState> = _state.asStateFlow()

    private var discoveryJob: Job? = null
    private var adapterStateJob: Job? = null

    init {
        observeAdapterState()
        loadPairedDevices()
    }

    override fun onIntent(intent: BluetoothIntent) {
        when (intent) {
            BluetoothIntent.StartDiscovery   -> startDiscovery()
            BluetoothIntent.StopDiscovery    -> stopDiscovery()
            BluetoothIntent.LoadPairedDevices -> loadPairedDevices()
            BluetoothIntent.DismissError     -> _state.update { it.copy(error = null) }
            BluetoothIntent.ToggleBluetooth  -> { /* Guide user to system settings — programmatic toggle removed in API 33 */ }
            is BluetoothIntent.ConnectDevice -> { /* TODO: BluetoothSocket connection in future sprint */ }
            is BluetoothIntent.UnpairDevice  -> { /* TODO: device.removeBond() via reflection */ }
        }
    }

    private fun observeAdapterState() {
        adapterStateJob = viewModelScope.launch {
            repository.observeAdapterState()
                .catch { /* Ignore observer errors */ }
                .collect { isEnabled ->
                    _state.update { it.copy(isBluetoothEnabled = isEnabled) }
                    if (isEnabled) loadPairedDevices()
                }
        }
    }

    private fun loadPairedDevices() {
        viewModelScope.launch {
            try {
                repository.getPairedDevices()
                    .catch { e -> _state.update { it.copy(error = e.message) } }
                    .collect { devices ->
                        _state.update { it.copy(pairedDevices = devices) }
                    }
            } catch (e: SecurityException) {
                _state.update { it.copy(error = "Bluetooth permission required") }
            }
        }
    }

    private fun startDiscovery() {
        if (!repository.isSupported) {
            _state.update { it.copy(error = "Bluetooth is not supported on this device") }
            return
        }
        if (!repository.isEnabled) {
            _state.update { it.copy(error = "Please enable Bluetooth to scan for devices") }
            return
        }

        discoveryJob?.cancel()
        discoveryJob = viewModelScope.launch {
            _state.update {
                it.copy(
                    isDiscovering    = true,
                    discoveredDevices = persistentListOf(),
                    error            = null,
                )
            }

            val accumulated = mutableListOf<BluetoothDeviceUiModel>()

            repository.startDiscovery()
                .catch { e ->
                    _state.update { it.copy(isDiscovering = false, error = e.message) }
                }
                .collect { device ->
                    // Deduplicate by MAC address
                    if (accumulated.none { it.address == device.address }) {
                        accumulated.add(device)
                        _state.update {
                            it.copy(discoveredDevices = accumulated.toImmutableList())
                        }
                    }
                }

            _state.update { it.copy(isDiscovering = false) }
        }
    }

    private fun stopDiscovery() {
        discoveryJob?.cancel()
        repository.stopDiscovery()
        _state.update { it.copy(isDiscovering = false) }
    }

    override fun onCleared() {
        super.onCleared()
        discoveryJob?.cancel()
        adapterStateJob?.cancel()
        repository.stopDiscovery()
    }
}
