package com.example.connectivityhub.feature.ble.ui

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
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.example.connectivityhub.core.permission.RequestPermissions
import com.example.connectivityhub.core.permission.blePermissions
import com.example.connectivityhub.feature.ble.BleDeviceUiModel
import com.example.connectivityhub.feature.ble.BleIntent
import com.example.connectivityhub.feature.ble.BleViewModel
import com.example.connectivityhub.theme.BleColor
import com.example.connectivityhub.theme.BleColorDim
import com.example.connectivityhub.theme.BorderSubtle
import com.example.connectivityhub.theme.MintTertiary
import com.example.connectivityhub.theme.NavyCard
import com.example.connectivityhub.theme.TextPrimary
import com.example.connectivityhub.theme.TextSecondary

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BleScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val vm: BleViewModel = viewModel(
        factory = viewModelFactory {
            initializer { BleViewModel(context) }
        }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    val infiniteTransition = rememberInfiniteTransition(label = "BlePulse")
    val ring1 by infiniteTransition.animateFloat(
        initialValue  = 0.6f,
        targetValue   = 1.4f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Restart),
        label         = "Ring1",
    )
    val ring2 by infiniteTransition.animateFloat(
        initialValue  = 0.6f,
        targetValue   = 1.4f,
        animationSpec = infiniteRepeatable(tween(2000, 700, easing = LinearEasing), RepeatMode.Restart),
        label         = "Ring2",
    )
    val ring3 by infiniteTransition.animateFloat(
        initialValue  = 0.6f,
        targetValue   = 1.4f,
        animationSpec = infiniteRepeatable(tween(2000, 1400, easing = LinearEasing), RepeatMode.Restart),
        label         = "Ring3",
    )

    RequestPermissions(
        permissions = blePermissions,
        onPermissionsResult = { _, _ -> }
    ) { _ ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(Color(0xFF060A08), Color(0xFF080C14))))
        ) {
        LazyColumn(
            modifier            = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(
                    modifier            = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Expanding rings animation
                    Box(modifier = Modifier.size(160.dp), contentAlignment = Alignment.Center) {
                        listOf(ring1, ring2, ring3).forEach { scale ->
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .scale(scale)
                                    .clip(CircleShape)
                                    .border(1.5.dp, BleColor.copy(alpha = (1.4f - scale) * 0.5f), CircleShape)
                            )
                        }
                        // Center icon
                        Box(
                            modifier         = Modifier.size(72.dp).clip(CircleShape).background(BleColorDim).border(2.dp, BleColor, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Nfc, "BLE", tint = BleColor, modifier = Modifier.size(36.dp))
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Bluetooth Low Energy", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                        Surface(shape = RoundedCornerShape(6.dp), color = BleColor.copy(0.2f)) {
                            Text(
                                text     = "BLE",
                                style    = MaterialTheme.typography.labelSmall,
                                color    = BleColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text(
                        text      = "Low-energy scanning for nearby peripherals.\nGATT connections coming in a future update.",
                        style     = MaterialTheme.typography.bodySmall,
                        color     = TextSecondary,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick  = { vm.onIntent(if (state.isScanning) BleIntent.StopScan else BleIntent.StartScan) },
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = BleColor,
                            contentColor   = Color(0xFF060A08),
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                    ) {
                        Icon(
                            imageVector = if (state.isScanning) Icons.Filled.Stop else Icons.Filled.Search,
                            contentDescription = null,
                            modifier    = Modifier.size(18.dp),
                        )
                        Text(
                            text       = if (state.isScanning) "Stop Scan" else "Scan BLE Devices",
                            modifier   = Modifier.padding(start = 8.dp),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            // ── Roadmap card ──
            item { BleRoadmapCard() }

            // ── Discovered BLE devices ──
            if (state.scannedDevices.isNotEmpty()) {
                item {
                    Text(
                        text  = "Scanned Devices (${state.scannedDevices.size})",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary,
                    )
                }
                items(state.scannedDevices, key = { it.address }) { device ->
                    BleDeviceItem(device = device)
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
}

@Composable
private fun BleRoadmapCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = NavyCard),
        border   = androidx.compose.foundation.BorderStroke(1.dp, BleColor.copy(0.25f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Upcoming BLE Features", style = MaterialTheme.typography.titleSmall, color = BleColor)
            listOf(
                "✓ BLE device scanning" to true,
                "⏳ GATT connection & service discovery" to false,
                "⏳ Read / write characteristics" to false,
                "⏳ Notifications & indications" to false,
                "⏳ BLE peripheral mode" to false,
            ).forEach { (text, done) ->
                Text(
                    text  = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (done) BleColor else TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun BleDeviceItem(device: BleDeviceUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = NavyCard),
        border   = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier         = Modifier.size(40.dp).clip(CircleShape).background(BleColor.copy(0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Nfc, null, tint = BleColor, modifier = Modifier.size(20.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(device.displayName, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                Text(device.address, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Text("${device.rssi} dBm", style = MaterialTheme.typography.labelSmall, color = BleColor)
        }
    }
}
