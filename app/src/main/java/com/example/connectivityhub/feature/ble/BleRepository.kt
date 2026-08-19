package com.example.connectivityhub.feature.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Repository for BLE (Bluetooth Low Energy) operations.
 *
 * Currently scaffolded — scanning is implemented.
 * GATT connection, service discovery, and characteristic read/write
 * will be implemented in a future sprint.
 *
 * Uses [BluetoothLeScanner] for device scanning and exposes results as [Flow].
 */
class BleRepository(private val context: Context) {

    private val bluetoothManager: BluetoothManager by lazy {
        context.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    private val adapter: BluetoothAdapter? get() = bluetoothManager.adapter
    private val leScanner: BluetoothLeScanner? get() = adapter?.bluetoothLeScanner

    /** Whether the device supports BLE. */
    val isBleSupported: Boolean
        get() = context.packageManager.hasSystemFeature(
            android.content.pm.PackageManager.FEATURE_BLUETOOTH_LE
        )

    /** Whether Bluetooth (and thus BLE) is currently enabled. */
    val isEnabled: Boolean get() = adapter?.isEnabled == true

    /**
     * Starts a BLE scan and emits discovered [BleDeviceUiModel]s via [Flow].
     * The scan runs until the flow is cancelled or [stopScan] is called.
     *
     * Scan settings use SCAN_MODE_LOW_LATENCY for fast discovery during
     * active user interaction.
     */
    @Suppress("MissingPermission")
    fun startScan(
        filters: List<ScanFilter> = emptyList(),
    ): Flow<BleDeviceUiModel> = callbackFlow {
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                trySend(result.toUiModel())
            }

            override fun onBatchScanResults(results: List<ScanResult>) {
                results.forEach { trySend(it.toUiModel()) }
            }

            override fun onScanFailed(errorCode: Int) {
                close(Exception("BLE scan failed with error code: $errorCode"))
            }
        }

        leScanner?.startScan(filters, settings, callback)
            ?: close(Exception("BLE scanner not available. Is Bluetooth enabled?"))

        awaitClose {
            try { leScanner?.stopScan(callback) } catch (_: Exception) {}
        }
    }

    /** Stops an ongoing BLE scan. */
    @Suppress("MissingPermission")
    fun stopScan(callback: ScanCallback? = null) {
        // When using callbackFlow, cancelling the flow handles this automatically.
        // This method is provided for manual control if needed.
    }

    // ─────────────────────────────────────────────────
    //  GATT Connection — Scaffolded for future sprint
    // ─────────────────────────────────────────────────

    /**
     * TODO (Future Sprint): Connect to a BLE peripheral via GATT.
     *
     * Steps to implement:
     * 1. Get BluetoothDevice by MAC address: adapter.getRemoteDevice(address)
     * 2. Call device.connectGatt(context, autoConnect, gattCallback)
     * 3. In onConnectionStateChange, discover services: gatt.discoverServices()
     * 4. In onServicesDiscovered, read/write characteristics as needed
     * 5. Implement BluetoothGattCallback and expose results as Flow
     */
    fun connectToDevice(address: String): Flow<BleConnectionState> {
        TODO("GATT connection will be implemented in a future sprint")
    }

    private fun ScanResult.toUiModel(): BleDeviceUiModel {
        val deviceName = try {
            @Suppress("MissingPermission")
            device.name ?: scanRecord?.deviceName ?: ""
        } catch (_: Exception) { "" }

        return BleDeviceUiModel(
            name    = deviceName,
            address = device.address ?: "",
            rssi    = rssi,
        )
    }
}
