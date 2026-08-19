package com.example.connectivityhub.feature.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

/**
 * Repository that wraps Android's [WifiManager] and exposes connectivity
 * information as Kotlin Flows.
 */
class WifiRepository(private val context: Context) {

    private val wifiManager: WifiManager by lazy {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private val connectivityManager: ConnectivityManager by lazy {
        context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    /** Whether WiFi is currently enabled. */
    fun isWifiEnabled(): Boolean = wifiManager.isWifiEnabled

    /**
     * Triggers a WiFi scan and emits the results as a [Flow].
     * Registers a [BroadcastReceiver] for [WifiManager.SCAN_RESULTS_AVAILABLE_ACTION]
     * and unregisters it when the flow is cancelled.
     */
    fun scanNetworks(): Flow<ImmutableList<WifiNetworkUiModel>> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                    val results = wifiManager.scanResults
                        .filter { it.SSID.isNotBlank() }
                        .sortedByDescending { it.level }
                        .map { it.toUiModel(connectedSsid = getConnectedSsid()) }
                        .toImmutableList()
                    trySend(results)
                }
            }
        }

        val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(receiver, filter)

        // Trigger the scan
        @Suppress("DEPRECATION")
        wifiManager.startScan()

        awaitClose { context.unregisterReceiver(receiver) }
    }

    /**
     * Returns a [Flow] that emits once with the cached scan results.
     * Useful for displaying previously cached networks without triggering a new scan.
     */
    fun getCachedNetworks(): Flow<ImmutableList<WifiNetworkUiModel>> = flow {
        val connectedSsid = getConnectedSsid()
        val results = wifiManager.scanResults
            .filter { it.SSID.isNotBlank() }
            .sortedByDescending { it.level }
            .map { it.toUiModel(connectedSsid = connectedSsid) }
            .toImmutableList()
        emit(results)
    }

    /** Returns the SSID of the currently connected network, or null. */
    fun getConnectedSsid(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val network = connectivityManager.activeNetwork ?: return null
            val caps = connectivityManager.getNetworkCapabilities(network) ?: return null
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                // On API 29+, use TransportInfo
                @Suppress("DEPRECATION")
                wifiManager.connectionInfo?.ssid?.removeQuotes()
            } else null
        } else {
            @Suppress("DEPRECATION")
            wifiManager.connectionInfo?.ssid?.removeQuotes()
        }
    }

    /** Returns the IP address of the current WiFi connection. */
    fun getConnectedIpAddress(): String? {
        @Suppress("DEPRECATION")
        val ip = wifiManager.connectionInfo?.ipAddress ?: return null
        return if (ip == 0) null
        else String.format(
            "%d.%d.%d.%d",
            ip and 0xff, ip shr 8 and 0xff, ip shr 16 and 0xff, ip shr 24 and 0xff
        )
    }

    private fun ScanResult.toUiModel(connectedSsid: String?): WifiNetworkUiModel {
        val rawSsid = this.SSID.removeQuotes()
        return WifiNetworkUiModel(
            ssid        = rawSsid,
            bssid       = this.BSSID ?: "",
            rssi        = this.level,
            isSecured   = this.capabilities.contains("WPA") ||
                          this.capabilities.contains("WEP") ||
                          this.capabilities.contains("PSK"),
            isConnected = rawSsid == connectedSsid,
            frequency   = this.frequency,
            capabilities = this.capabilities,
        )
    }

    private fun String.removeQuotes(): String =
        this.removePrefix("\"").removeSuffix("\"").trim()
}
