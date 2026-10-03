package ir.bordermanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import ir.bordermanager.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: AppViewModel = viewModel()
            BorderTheme(vm.themeMode) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    if (vm.isLoggedIn) {
                        BackHandler(enabled = vm.currentScreen !is Screen.Home) { vm.back() }
                        AppRoot(vm)
                    } else {
                        AccountGateScreen(vm)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppRoot(vm: AppViewModel) {
    Scaffold(bottomBar = { MainBottomBar(vm) }) { inner ->
        when (val screen = vm.currentScreen) {
            Screen.Home -> HomeScreen(vm, Modifier.padding(inner))
            Screen.Owners -> OwnersScreen(vm, Modifier.padding(inner))
            is Screen.OwnerDetail -> OwnerDetailScreen(vm, screen.ownerId, Modifier.padding(inner))
            is Screen.CargoDetail -> CargoDetailScreen(vm, screen.cargoId, Modifier.padding(inner))
            is Screen.TruckForm -> TruckFormScreen(vm, screen.cargoId, screen.truckId, Modifier.padding(inner))
            Screen.Accounting -> AccountingScreen(vm, Modifier.padding(inner))
            Screen.Reports -> ReportsScreen(vm, Modifier.padding(inner))
            is Screen.OwnerAccounting -> OwnerAccountingScreen(vm, screen.ownerId, screen.year, screen.month, Modifier.padding(inner))
            Screen.Calendar -> CalendarScreen(vm, Modifier.padding(inner))
            Screen.Status -> StatusScreen(vm, Modifier.padding(inner))
            Screen.CallHistory -> CallHistoryScreen(vm, Modifier.padding(inner))
            Screen.Parking -> StatusListScreen(vm, parking = true, Modifier.padding(inner))
            Screen.Waiting -> StatusListScreen(vm, parking = false, Modifier.padding(inner))
            Screen.Settings -> SettingsScreen(vm, Modifier.padding(inner))
            Screen.Borders -> BordersScreen(vm, Modifier.padding(inner))
        }
    }
}

@Composable
private fun MainBottomBar(vm: AppViewModel) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
        val current = vm.currentScreen
        NavigationBarItem(selected = current is Screen.Home, onClick = vm::home, icon = { Icon(Icons.Default.Home, null) }, label = { Text("خانه", fontSize=10.sp) })
        NavigationBarItem(selected = current is Screen.Owners || current is Screen.OwnerDetail || current is Screen.CargoDetail || current is Screen.TruckForm, onClick = vm::owners, icon = { Icon(Icons.Default.Groups, null) }, label = { Text("صاحبان کالا", fontSize=10.sp) })
        NavigationBarItem(selected = current is Screen.Status || current is Screen.CallHistory || current is Screen.Parking || current is Screen.Waiting, onClick = vm::status, icon = { Icon(Icons.Default.Inventory2, null) }, label = { Text("وضعیت کالا", fontSize=10.sp) })
        NavigationBarItem(selected = current is Screen.Accounting || current is Screen.OwnerAccounting, onClick = vm::accounting, icon = { Icon(Icons.Default.ReceiptLong, null) }, label = { Text("حسابرسی", fontSize=10.sp) })
        NavigationBarItem(selected = current is Screen.Reports, onClick = vm::reports, icon = { Icon(Icons.Default.Assessment, null) }, label = { Text("گزارش‌ها", fontSize=10.sp) })
        NavigationBarItem(selected = current is Screen.Settings || current is Screen.Borders, onClick = vm::settings, icon = { Icon(Icons.Default.MoreHoriz, null) }, label = { Text("بیشتر", fontSize=10.sp) })
    }
}
