package com.example.connectivityhub

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Bluetooth
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.connectivityhub.feature.ble.ui.BleScreen
import com.example.connectivityhub.feature.bluetooth.ui.BluetoothScreen
import com.example.connectivityhub.feature.home.HomeScreen
import com.example.connectivityhub.feature.wifi.ui.WifiScreen
import com.example.connectivityhub.theme.CyanPrimary
import com.example.connectivityhub.theme.NavySurface
import com.example.connectivityhub.theme.NavySurfaceVariant
import com.example.connectivityhub.theme.TextSecondary

/** Bottom navigation destinations. */
data class NavDestination(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val contentDescription: String,
)

private val destinations = listOf(
    NavDestination("Home",      Icons.Filled.Hub,       Icons.Outlined.Hub,       "Home"),
    NavDestination("WiFi",      Icons.Filled.Wifi,      Icons.Outlined.Wifi,      "WiFi"),
    NavDestination("Bluetooth", Icons.Filled.Bluetooth, Icons.Outlined.Bluetooth, "Bluetooth"),
    NavDestination("BLE",       Icons.Filled.Nfc,       Icons.Outlined.Nfc,       "BLE"),
)

@Composable
fun MainNavigation() {
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
    val previousIndex = remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        bottomBar = {
            ConnectivityBottomBar(
                selectedIndex = selectedIndex,
                destinations = destinations,
                onDestinationSelected = { index ->
                    previousIndex.intValue = selectedIndex
                    selectedIndex = index
                },
            )
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedIndex,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            transitionSpec = {
                val direction = if (targetState > initialState) 1 else -1
                (slideInHorizontally(
                    animationSpec = tween(300),
                    initialOffsetX = { it * direction },
                ) + fadeIn(tween(300))) togetherWith
                (slideOutHorizontally(
                    animationSpec = tween(300),
                    targetOffsetX = { -it * direction },
                ) + fadeOut(tween(150)))
            },
            label = "MainNavAnimation",
        ) { index ->
            when (index) {
                0 -> HomeScreen(
                    onNavigateToWifi      = { selectedIndex = 1 },
                    onNavigateToBluetooth = { selectedIndex = 2 },
                    onNavigateToBle       = { selectedIndex = 3 },
                )
                1 -> WifiScreen()
                2 -> BluetoothScreen()
                3 -> BleScreen()
            }
        }
    }
}

@Composable
private fun ConnectivityBottomBar(
    selectedIndex: Int,
    destinations: List<NavDestination>,
    onDestinationSelected: (Int) -> Unit,
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = NavySurface,
        tonalElevation = androidx.compose.ui.unit.Dp(0f),
    ) {
        destinations.forEachIndexed { index, dest ->
            val selected = selectedIndex == index
            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                        contentDescription = dest.contentDescription,
                    )
                },
                label = { Text(dest.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = CyanPrimary,
                    selectedTextColor   = CyanPrimary,
                    indicatorColor      = NavySurfaceVariant,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary,
                ),
            )
        }
    }
}
