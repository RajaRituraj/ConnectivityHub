package com.example.connectivityhub.core.permission

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

/**
 * Required permissions for WiFi scanning.
 * ACCESS_FINE_LOCATION is needed to retrieve scan results on API 26+.
 */
val wifiPermissions: List<String> = listOf(
    android.Manifest.permission.ACCESS_FINE_LOCATION,
    android.Manifest.permission.ACCESS_COARSE_LOCATION,
    android.Manifest.permission.ACCESS_WIFI_STATE,
    android.Manifest.permission.CHANGE_WIFI_STATE,
)

/**
 * Required permissions for Bluetooth Classic discovery and connection.
 * On API 31+ BLUETOOTH_SCAN and BLUETOOTH_CONNECT replace legacy permissions.
 */
val bluetoothPermissions: List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    listOf(
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT,
    )
} else {
    listOf(
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.BLUETOOTH_ADMIN,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
    )
}

/**
 * Required permissions for BLE scanning and advertising.
 */
val blePermissions: List<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    listOf(
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.BLUETOOTH_ADVERTISE,
    )
} else {
    listOf(
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
    )
}

/**
 * Composable that automatically requests a list of permissions when first composed
 * and calls [onPermissionsResult] with the granted state.
 *
 * @param permissions  List of permission strings to request.
 * @param onPermissionsResult  Callback: (allGranted: Boolean, state: MultiplePermissionsState) -> Unit
 * @param content  The content to show after permissions are resolved.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissions(
    permissions: List<String>,
    onPermissionsResult: (allGranted: Boolean, state: MultiplePermissionsState) -> Unit,
    content: @Composable (MultiplePermissionsState) -> Unit,
) {
    val permissionsState = rememberMultiplePermissionsState(permissions = permissions) { results ->
        onPermissionsResult(results.values.all { it }, permissionsState)
    }

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    content(permissionsState)
}

/** Returns true if all permissions in [permissions] are currently granted. */
@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.areAllGranted(): Boolean = allPermissionsGranted

/** Returns a human-readable description of which permissions are still needed. */
@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.deniedPermissionNames(): List<String> =
    revokedPermissions.map { it.permission.substringAfterLast('.') }
