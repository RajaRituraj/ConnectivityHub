package com.example.connectivityhub.feature.bluetooth.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.connectivityhub.feature.bluetooth.BluetoothDeviceCategory
import com.example.connectivityhub.feature.bluetooth.BluetoothDeviceUiModel
import com.example.connectivityhub.feature.bluetooth.BluetoothIntent
import com.example.connectivityhub.feature.bluetooth.BluetoothState
import com.example.connectivityhub.feature.bluetooth.BluetoothViewModel
import com.example.connectivityhub.theme.BluetoothColor
import com.example.connectivityhub.theme.BluetoothColorDim
import com.example.connectivityhub.theme.BorderSubtle
import com.example.connectivityhub.theme.ErrorRed
import com.example.connectivityhub.theme.NavyCard
import com.example.connectivityhub.theme.SuccessGreen
import com.example.connectivityhub.theme.TextPrimary
import com.example.connectivityhub.theme.TextSecondary

@Composable
fun BluetoothScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val vm: BluetoothViewModel = viewModel(
        factory = androidx.lifecycle.viewmodel.initializer { BluetoothViewModel(context) }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { vm.onIntent(BluetoothIntent.LoadPairedDevices) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF06090F), Color(0xFF080C14))))
    ) {
        LazyColumn(
            modifier            = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                BluetoothHeader(state = state, vm = vm)
            }

            if (state.error != null) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f)),
                        border   = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(0.4f)),
                    ) {
                        Text(
                            text     = state.error!!,
                            style    = MaterialTheme.typography.bodySmall,
                            color    = ErrorRed,
                            modifier = Modifier.padding(12.dp),
                        )
                    }
                }
            }

            if (state.pairedDevices.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Paired Devices",
                        count = state.pairedDevices.size,
                        color = SuccessGreen,
                    )
                }
                items(state.pairedDevices, key = { it.address }) { device ->
                    BluetoothDeviceItem(device = device, accentColor = SuccessGreen)
                }
            }

            if (state.discoveredDevices.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Discovered Devices",
                        count = state.discoveredDevices.size,
                        color = BluetoothColor,
                    )
                }
                items(state.discoveredDevices, key = { it.address }) { device ->
                    BluetoothDeviceItem(device = device, accentColor = BluetoothColor)
                }
            }

            if (!state.isDiscovering && state.discoveredDevices.isEmpty() && state.pairedDevices.isEmpty()) {
                item {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(Icons.Filled.Bluetooth, null, tint = TextSecondary, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No devices found", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
                        Text("Tap Discover to scan for nearby devices", style = MaterialTheme.typography.bodySmall, color = TextSecondary.copy(0.6f))
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun BluetoothHeader(state: BluetoothState, vm: BluetoothViewModel) {
    val infiniteTransition = rememberInfiniteTransition(label = "Radar")
    val rotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = if (state.isDiscovering) 360f else 0f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label         = "RadarRotation",
    )
    val outerPulse by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = if (state.isDiscovering) 1.25f else 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label         = "OuterPulse",
    )

    Column(
        modifier            = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(outerPulse)
                    .clip(CircleShape)
                    .background(BluetoothColor.copy(alpha = 0.07f))
                    .border(1.dp, BluetoothColor.copy(alpha = 0.3f), CircleShape)
            )
            Box(
                modifier         = Modifier.size(88.dp).clip(CircleShape).background(BluetoothColorDim).border(2.dp, BluetoothColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Filled.Bluetooth,
                    contentDescription = "Bluetooth",
                    tint               = BluetoothColor,
                    modifier           = Modifier.size(36.dp).rotate(rotation),
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Bluetooth", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        Text(
            text  = if (state.isDiscovering) "Discovering devices..." else "Classic & Low Energy",
            style = MaterialTheme.typography.bodyMedium,
            color = BluetoothColor,
        )
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick  = { vm.onIntent(if (state.isDiscovering) BluetoothIntent.StopDiscovery else BluetoothIntent.StartDiscovery) },
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = BluetoothColor,
                    contentColor   = Color(0xFF060912),
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            ) {
                Icon(
                    imageVector        = if (state.isDiscovering) Icons.Filled.Stop else Icons.Filled.Search,
                    contentDescription = null,
                    modifier           = Modifier.size(18.dp),
                )
                Text(
                    text       = if (state.isDiscovering) "Stop" else "Discover",
                    modifier   = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, count: Int, color: Color) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleSmall, color = TextSecondary)
        Box(
            modifier = Modifier.clip(CircleShape).background(color.copy(0.15f)).padding(horizontal = 8.dp, vertical = 2.dp),
        ) {
            Text(count.toString(), style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

@Composable
fun BluetoothDeviceItem(
    device: BluetoothDeviceUiModel,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = NavyCard),
        border   = androidx.compose.foundation.BorderStroke(1.dp, if (device.isConnected) accentColor.copy(0.5f) else BorderSubtle),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier         = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(accentColor.copy(0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = device.category.icon(),
                    contentDescription = null,
                    tint               = accentColor,
                    modifier           = Modifier.size(24.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(device.displayName, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                Text(device.address, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            Column(horizontalAlignment = Alignment.End) {
                if (device.rssi != 0) {
                    Text("${device.rssi} dBm", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                if (device.isPaired) {
                    Text("Paired", style = MaterialTheme.typography.labelSmall, color = SuccessGreen)
                }
            }
        }
    }
}

private fun BluetoothDeviceCategory.icon(): ImageVector = when (this) {
    BluetoothDeviceCategory.AUDIO    -> Icons.Filled.Headphones
    BluetoothDeviceCategory.PHONE    -> Icons.Filled.Phone
    BluetoothDeviceCategory.COMPUTER -> Icons.Filled.Computer
    BluetoothDeviceCategory.WEARABLE -> Icons.Filled.Watch
    BluetoothDeviceCategory.UNKNOWN  -> Icons.Filled.Devices
}
