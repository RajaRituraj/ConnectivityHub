package com.example.connectivityhub.feature.wifi.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.connectivityhub.feature.wifi.WifiIntent
import com.example.connectivityhub.feature.wifi.WifiNetworkUiModel
import com.example.connectivityhub.feature.wifi.WifiState
import com.example.connectivityhub.feature.wifi.WifiViewModel
import com.example.connectivityhub.theme.BorderSubtle
import com.example.connectivityhub.theme.CyanPrimary
import com.example.connectivityhub.theme.CyanPrimaryDark
import com.example.connectivityhub.theme.ErrorRed
import com.example.connectivityhub.theme.NavyCard
import com.example.connectivityhub.theme.NavySurface
import com.example.connectivityhub.theme.SignalFair
import com.example.connectivityhub.theme.SignalFull
import com.example.connectivityhub.theme.SignalGood
import com.example.connectivityhub.theme.SignalNone
import com.example.connectivityhub.theme.SignalPoor
import com.example.connectivityhub.theme.SuccessGreen
import com.example.connectivityhub.theme.TextPrimary
import com.example.connectivityhub.theme.TextSecondary
import com.example.connectivityhub.theme.WifiColor
import com.example.connectivityhub.theme.WifiColorDim

@Composable
fun WifiScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val vm: WifiViewModel = viewModel(
        factory = androidx.lifecycle.viewmodel.initializer { WifiViewModel(context) }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    // Auto-scan on first entry
    LaunchedEffect(Unit) { vm.onIntent(WifiIntent.ScanNetworks) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF060D1A), Color(0xFF080C14))))
    ) {
        LazyColumn(
            modifier            = Modifier.fillMaxSize().statusBarsPadding(),
            contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // ── Header ──
            item {
                WifiHeader(state = state, onScan = { vm.onIntent(WifiIntent.ScanNetworks) })
            }

            // ── Error Banner ──
            if (state.error != null) {
                item {
                    ErrorBanner(message = state.error!!, onDismiss = { vm.onIntent(WifiIntent.DismissError) })
                }
            }

            // ── Connected network ──
            if (state.connectedSsid != null) {
                item {
                    ConnectedNetworkCard(ssid = state.connectedSsid!!, ip = state.connectedIpAddress)
                }
            }

            // ── Networks list ──
            if (state.networks.isNotEmpty()) {
                item {
                    Text(
                        text  = "Available Networks (${state.networks.size})",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextSecondary,
                    )
                }
                items(items = state.networks, key = { it.bssid.ifBlank { it.ssid } }) { network ->
                    WifiNetworkItem(network = network)
                }
            } else if (!state.isScanning && !state.isLoading) {
                item { EmptyNetworkState(onScan = { vm.onIntent(WifiIntent.ScanNetworks) }) }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun WifiHeader(state: WifiState, onScan: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "ScanPulse")
    val outerScale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = if (state.isScanning) 1.3f else 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label         = "OuterPulse",
    )

    Column(
        modifier            = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Animated scan button
        Box(
            modifier            = Modifier.size(120.dp),
            contentAlignment    = Alignment.Center,
        ) {
            // Outer pulse ring
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(outerScale)
                    .clip(CircleShape)
                    .background(WifiColor.copy(alpha = 0.08f))
                    .border(1.dp, WifiColor.copy(alpha = 0.3f), CircleShape),
            )
            // Inner button
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(if (state.isScanning) WifiColorDim else WifiColor.copy(alpha = 0.15f))
                    .border(2.dp, WifiColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                if (state.isScanning) {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(36.dp),
                        color     = WifiColor,
                        strokeCap = StrokeCap.Round,
                    )
                } else {
                    Icon(
                        imageVector        = Icons.Filled.Wifi,
                        contentDescription = "Scan",
                        tint               = WifiColor,
                        modifier           = Modifier.size(36.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text  = "WiFi Networks",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
        )
        Text(
            text  = if (state.isScanning) "Scanning..." else "Tap to scan",
            style = MaterialTheme.typography.bodyMedium,
            color = WifiColor,
        )
        Spacer(Modifier.height(16.dp))

        Button(
            onClick  = onScan,
            enabled  = !state.isScanning,
            shape    = RoundedCornerShape(12.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = WifiColor,
                contentColor           = Color(0xFF060D1A),
                disabledContainerColor = WifiColorDim,
                disabledContentColor   = TextSecondary,
            ),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
        ) {
            Icon(Icons.Filled.Refresh, "Scan", Modifier.size(18.dp))
            Text(
                text     = if (state.isScanning) "Scanning..." else "Scan Networks",
                modifier = Modifier.padding(start = 8.dp),
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun ConnectedNetworkCard(ssid: String, ip: String?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = Color(0xFF003A4A)),
        border   = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier            = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text("Connected", style = MaterialTheme.typography.labelMedium, color = SuccessGreen)
                Text(ssid, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                if (ip != null) Text(ip, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Box(
                modifier         = Modifier.size(40.dp).clip(CircleShape).background(SuccessGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Wifi, "Connected", tint = SuccessGreen, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Composable
private fun EmptyNetworkState(onScan: () -> Unit) {
    Column(
        modifier            = Modifier.fillMaxWidth().padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(Icons.Filled.Wifi, null, tint = TextSecondary, modifier = Modifier.size(64.dp))
        Text("No networks found", style = MaterialTheme.typography.titleMedium, color = TextSecondary)
        Text("Tap scan to discover WiFi networks nearby", style = MaterialTheme.typography.bodySmall, color = TextSecondary.copy(alpha = 0.6f))
    }
}

@Composable
private fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(12.dp),
        colors   = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.12f)),
        border   = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.4f)),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
        ) {
            Text(message, style = MaterialTheme.typography.bodySmall, color = ErrorRed, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun WifiNetworkItem(network: WifiNetworkUiModel, modifier: Modifier = Modifier) {
    val signalColor = when (network.signalLevel) {
        4    -> SignalFull
        3    -> SignalGood
        2    -> SignalFair
        1    -> SignalPoor
        else -> SignalNone
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(
            containerColor = if (network.isConnected) Color(0xFF003A4A) else NavyCard
        ),
        border   = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (network.isConnected) CyanPrimary.copy(alpha = 0.5f) else BorderSubtle,
        ),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Signal bars
            SignalBars(level = network.signalLevel, color = signalColor)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text  = network.ssid,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (network.isConnected) CyanPrimary else TextPrimary,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                ) {
                    Text(
                        text  = "${network.rssi} dBm",
                        style = MaterialTheme.typography.labelSmall,
                        color = signalColor,
                    )
                    Text("•", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text(
                        text  = network.frequencyBand,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                    )
                }
            }

            // Lock icon
            Icon(
                imageVector        = if (network.isSecured) Icons.Filled.Lock else Icons.Filled.LockOpen,
                contentDescription = if (network.isSecured) "Secured" else "Open",
                tint               = if (network.isSecured) TextSecondary else SuccessGreen,
                modifier           = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun SignalBars(level: Int, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier              = modifier,
        verticalAlignment     = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        val heights = listOf(6.dp, 10.dp, 14.dp, 18.dp)
        heights.forEachIndexed { index, height ->
            val isActive = index < level
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = height)
                    .clip(RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    .background(if (isActive) color else color.copy(alpha = 0.2f))
            )
        }
    }
}
