package com.example.connectivityhub.feature.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

/**
 * Repository that wraps Android's [BluetoothAdapter] and exposes
 * Bluetooth Classic operations as Kotlin Flows.
 */
@SuppressLint("MissingPermission") // Permissions are handled at the UI layer
class BluetoothRepository(private val context: Context) {

    private val bluetoothManager: BluetoothManager by lazy {
        context.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    val adapter: BluetoothAdapter? get() = bluetoothManager.adapter

    /** Whether Bluetooth hardware is available on this device. */
    val isSupported: Boolean get() = adapter != null

    /** Whether Bluetooth is currently enabled. */
    val isEnabled: Boolean get() = try {
        adapter?.isEnabled == true
    } catch (_: SecurityException) {
        false
    }

    /**
     * Returns all paired (bonded) devices as a [Flow].
     */
    fun getPairedDevices(): Flow<ImmutableList<BluetoothDeviceUiModel>> = flow {
        val bonded = adapter?.bondedDevices
            ?.map { it.toUiModel(isPaired = true) }
            ?.toImmutableList()
            ?: kotlinx.collections.immutable.persistentListOf()
        emit(bonded)
    }

    /**
     * Starts Bluetooth Classic discovery and emits newly found devices via [Flow].
     * Automatically stops discovery when the flow is cancelled.
     */
    fun startDiscovery(): Flow<BluetoothDeviceUiModel> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothDevice.ACTION_FOUND -> {
                        val device: BluetoothDevice? =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                intent.getParcelableExtra(
                                    BluetoothDevice.EXTRA_DEVICE,
                                    BluetoothDevice::class.java,
                                )
                            } else {
                                @Suppress("DEPRECATION")
                                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            }
                        val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE).toInt()
                        device?.let { trySend(it.toUiModel(rssi = rssi)) }
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        close() // Signal completion when discovery finishes
                    }
                }
            }
        }

        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(receiver, filter)

        try {
            if (adapter?.isDiscovering == true) adapter?.cancelDiscovery()
            adapter?.startDiscovery()
        } catch (e: SecurityException) {
            close(e)
            return@callbackFlow
        }

        awaitClose {
            adapter?.cancelDiscovery()
            try { context.unregisterReceiver(receiver) } catch (_: Exception) {}
        }
    }

    /** Cancels an ongoing Bluetooth discovery scan. */
    fun stopDiscovery() {
        try {
            adapter?.cancelDiscovery()
        } catch (e: SecurityException) {
            // Ignore if permission is missing
        }
    }

    /** Returns a [Flow] of adapter state changes (enabled/disabled). */
    fun observeAdapterState(): Flow<Boolean> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                    val state = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR,
                    )
                    trySend(state == BluetoothAdapter.STATE_ON)
                }
            }
        }
        context.registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        trySend(isEnabled) // Emit current state immediately
        awaitClose { try { context.unregisterReceiver(receiver) } catch (_: Exception) {} }
    }

    @SuppressLint("MissingPermission")
    private fun BluetoothDevice.toUiModel(
        rssi: Int = 0,
        isPaired: Boolean = false,
    ): BluetoothDeviceUiModel = BluetoothDeviceUiModel(
        name        = this.name ?: "",
        address     = this.address ?: "",
        rssi        = rssi,
        isPaired    = isPaired || this.bondState == BluetoothDevice.BOND_BONDED,
        isConnected = false, // Classic connection state requires a profile proxy (future work)
        deviceClass = this.bluetoothClass?.deviceClass ?: 0,
    )
}
