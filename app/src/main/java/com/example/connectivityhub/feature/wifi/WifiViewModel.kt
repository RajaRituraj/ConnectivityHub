package com.example.connectivityhub.feature.wifi

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

class WifiViewModel(private val context: Context) : MviViewModel<WifiIntent, WifiState>() {

    private val repository = WifiRepository(context)

    private val _state = MutableStateFlow(WifiState())
    override val state: StateFlow<WifiState> = _state.asStateFlow()

    private var scanJob: Job? = null

    init {
        // Load initial state
        _state.update {
            it.copy(
                isWifiEnabled  = repository.isWifiEnabled(),
                connectedSsid  = repository.getConnectedSsid(),
                connectedIpAddress = repository.getConnectedIpAddress(),
            )
        }
        // Load cached networks immediately
        loadCachedNetworks()
    }

    override fun onIntent(intent: WifiIntent) {
        when (intent) {
            WifiIntent.ScanNetworks    -> startScan()
            WifiIntent.ToggleWifi      -> { /* Toggling WiFi programmatically deprecated in API 29+ — guide user to Settings */ }
            WifiIntent.DisconnectNetwork -> { /* TODO: disconnect via NetworkRequest */ }
            WifiIntent.DismissError    -> _state.update { it.copy(error = null) }
            is WifiIntent.ConnectToNetwork -> { /* TODO: WifiNetworkSpecifier for API 29+ */ }
        }
    }

    private fun loadCachedNetworks() {
        viewModelScope.launch {
            repository.getCachedNetworks()
                .catch { e -> _state.update { it.copy(error = e.message) } }
                .collect { networks ->
                    _state.update { it.copy(networks = networks) }
                }
        }
    }

    private fun startScan() {
        scanJob?.cancel()
        scanJob = viewModelScope.launch {
            _state.update { it.copy(isScanning = true, error = null) }

            repository.scanNetworks()
                .catch { e ->
                    _state.update { it.copy(isScanning = false, error = e.message) }
                }
                .collect { networks ->
                    _state.update {
                        it.copy(
                            isScanning    = false,
                            networks      = networks,
                            isWifiEnabled = repository.isWifiEnabled(),
                            connectedSsid = repository.getConnectedSsid(),
                            connectedIpAddress = repository.getConnectedIpAddress(),
                        )
                    }
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        scanJob?.cancel()
    }
}
