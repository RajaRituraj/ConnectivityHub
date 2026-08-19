package com.example.connectivityhub.feature.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.connectivityhub.theme.BleColor
import com.example.connectivityhub.theme.BleColorDim
import com.example.connectivityhub.theme.BluetoothColor
import com.example.connectivityhub.theme.BluetoothColorDim
import com.example.connectivityhub.theme.BorderSubtle
import com.example.connectivityhub.theme.CyanPrimary
import com.example.connectivityhub.theme.NavyCard
import com.example.connectivityhub.theme.SuccessGreen
import com.example.connectivityhub.theme.TextPrimary
import com.example.connectivityhub.theme.TextSecondary
import com.example.connectivityhub.theme.WifiColor
import com.example.connectivityhub.theme.WifiColorDim

@Composable
fun HomeScreen(
    onNavigateToWifi: () -> Unit,
    onNavigateToBluetooth: () -> Unit,
    onNavigateToBle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val vm: HomeViewModel = viewModel(
        factory = androidx.lifecycle.viewmodel.initializer { HomeViewModel(context) }
    )
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { vm.onIntent(HomeIntent.RefreshStatus) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A1628), Color(0xFF080C14)),
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(24.dp))

            // ── Header ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "ConnectivityHub",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                    )
                    Text(
                        text = "Manage your connections",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
                IconButton(onClick = { vm.onIntent(HomeIntent.RefreshStatus) }) {
                    Icon(Icons.Filled.Refresh, "Refresh", tint = CyanPrimary)
                }
            }

            Spacer(Modifier.height(32.dp))

            // ── Status Summary ──
            StatusSummaryCard(state = state)

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Features",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            FeatureCard(
                title       = "WiFi",
                subtitle    = if (state.isWifiEnabled) state.connectedSsid ?: "Not connected" else "Disabled",
                icon        = if (state.isWifiEnabled) Icons.Filled.Wifi else Icons.Filled.WifiOff,
                accentColor = WifiColor,
                dimColor    = WifiColorDim,
                isEnabled   = state.isWifiEnabled,
                onClick     = onNavigateToWifi,
            )

            Spacer(Modifier.height(12.dp))

            FeatureCard(
                title       = "Bluetooth",
                subtitle    = if (state.isBluetoothEnabled) state.connectedBtDevice ?: "Ready to scan" else "Disabled",
                icon        = if (state.isBluetoothEnabled) Icons.Filled.Bluetooth else Icons.Filled.BluetoothDisabled,
                accentColor = BluetoothColor,
                dimColor    = BluetoothColorDim,
                isEnabled   = state.isBluetoothEnabled,
                onClick     = onNavigateToBluetooth,
            )

            Spacer(Modifier.height(12.dp))

            FeatureCard(
                title       = "BLE",
                subtitle    = if (state.isBleSupported) "Scan & Coming Features" else "Not supported",
                icon        = Icons.Filled.Nfc,
                accentColor = BleColor,
                dimColor    = BleColorDim,
                isEnabled   = state.isBleSupported,
                badge       = "BETA",
                onClick     = onNavigateToBle,
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatusSummaryCard(state: HomeState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(20.dp),
        colors   = CardDefaults.cardColors(containerColor = NavyCard),
        border   = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("System Status", style = MaterialTheme.typography.titleSmall, color = TextSecondary)
            Spacer(Modifier.height(16.dp))
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                StatusIndicator("WiFi",      state.isWifiEnabled,      WifiColor)
                StatusIndicator("Bluetooth", state.isBluetoothEnabled, BluetoothColor)
                StatusIndicator("BLE",       state.isBleSupported,     BleColor)
            }
            if (state.connectedSsid != null) {
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(SuccessGreen))
                    Text(
                        text  = "Connected to ${state.connectedSsid}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SuccessGreen,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusIndicator(label: String, isActive: Boolean, activeColor: Color) {
    val animatedColor by animateColorAsState(
        targetValue  = if (isActive) activeColor else Color(0xFF253650),
        animationSpec = tween(500),
        label        = "StatusColor",
    )
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse$label")
    val scale by infiniteTransition.animateFloat(
        initialValue  = 1f,
        targetValue   = if (isActive) 1.15f else 1f,
        animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
        label         = "Scale$label",
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier         = Modifier
                .size(48.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(animatedColor.copy(alpha = 0.15f))
                .border(1.dp, animatedColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(animatedColor))
        }
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = if (isActive) activeColor else TextSecondary)
        Text(
            text  = if (isActive) "ON" else "OFF",
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive) activeColor.copy(0.7f) else TextSecondary.copy(0.5f),
        )
    }
}

@Composable
private fun FeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    dimColor: Color,
    isEnabled: Boolean,
    onClick: () -> Unit,
    badge: String? = null,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape    = RoundedCornerShape(16.dp),
        colors   = CardDefaults.cardColors(containerColor = NavyCard),
        border   = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isEnabled) accentColor.copy(alpha = 0.3f) else BorderSubtle,
        ),
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier         = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isEnabled) dimColor else Color(0xFF1A2540)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = title,
                    tint               = if (isEnabled) accentColor else TextSecondary,
                    modifier           = Modifier.size(28.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                    if (badge != null) {
                        Surface(shape = RoundedCornerShape(4.dp), color = accentColor.copy(alpha = 0.2f)) {
                            Text(
                                text     = badge,
                                style    = MaterialTheme.typography.labelSmall,
                                color    = accentColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            )
                        }
                    }
                }
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = if (isEnabled) accentColor.copy(0.8f) else TextSecondary)
            }

            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Open $title",
                tint               = accentColor.copy(alpha = 0.5f),
                modifier           = Modifier.size(16.dp),
            )
        }
    }
}
