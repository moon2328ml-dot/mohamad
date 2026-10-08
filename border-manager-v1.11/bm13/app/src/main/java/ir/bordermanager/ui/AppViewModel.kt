package ir.bordermanager.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ir.bordermanager.BorderManagerApp
import ir.bordermanager.data.AppPreferences
import ir.bordermanager.data.AppRepository
import ir.bordermanager.data.CargoStatus
import ir.bordermanager.util.Jalali
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface Screen {
    data object Home : Screen
    data object Owners : Screen
    data class OwnerDetail(val ownerId: Long) : Screen
    data class CargoDetail(val cargoId: Long) : Screen
    data class Messaging(val cargoId: Long) : Screen
    data class TruckForm(val cargoId: Long, val truckId: Long? = null) : Screen
    data object Accounting : Screen
    data object Reports : Screen
    data class OwnerAccounting(val ownerId: Long, val year: Int, val month: Int) : Screen
    data object Calendar : Screen
    data object Status : Screen
    data object CallHistory : Screen
    data object Parking : Screen
    data object Waiting : Screen
    data object Settings : Screen
    data object Borders : Screen
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    val repo: AppRepository = (application as BorderManagerApp).repository
    val prefs = AppPreferences(application)
    private val stack = mutableStateListOf<Screen>(Screen.Home)
    var selectedBorderId by mutableStateOf<Long?>(null)
    var themeMode by mutableStateOf(prefs.themeMode)
    var compactMode by mutableStateOf(prefs.compactMode)
    var accountingYear by mutableStateOf(Jalali.now().date.year)
    var accountingMonth by mutableStateOf(Jalali.now().date.month)
    var calendarYear by mutableStateOf(Jalali.now().date.year)
    var calendarMonth by mutableStateOf(Jalali.now().date.month)
    var selectedCalendarDay by mutableStateOf(Jalali.now().date.day)

    var isLoggedIn by mutableStateOf(repo.cloud.session.loggedIn)
        private set
    var accountEmail by mutableStateOf(repo.cloud.session.email)
        private set
    var authBusy by mutableStateOf(false)
        private set
    var authMessage by mutableStateOf("")
        private set
    var syncBusy by mutableStateOf(false)
        private set
    var syncMessage by mutableStateOf("")
        private set

    init {
        if (isLoggedIn) {
            viewModelScope.launch {
                syncBusy = true
                val r = withContext(Dispatchers.IO) { repo.cloud.bootstrap() }
                if (r.restored) repo.changed()
                syncMessage = r.message
                syncBusy = false
            }
        }
    }

    val currentScreen: Screen get() = stack.last()
    fun navigate(screen: Screen) { if (stack.lastOrNull() != screen) stack += screen }
    fun back(): Boolean = if (stack.size > 1) { stack.removeAt(stack.lastIndex); true } else false
    fun home() { stack.clear(); stack += Screen.Home }
    fun owners() { stack.clear(); stack += Screen.Home; stack += Screen.Owners }
    fun status() { stack.clear(); stack += Screen.Home; stack += Screen.Status }
    fun accounting() { stack.clear(); stack += Screen.Home; stack += Screen.Accounting }
    fun reports() { stack.clear(); stack += Screen.Home; stack += Screen.Reports }
    fun settings() { stack.clear(); stack += Screen.Home; stack += Screen.Settings }

    fun setTheme(mode: String) { themeMode = mode; prefs.themeMode = mode }
    fun setCompact(value: Boolean) { compactMode = value; prefs.compactMode = value }

    fun previousAccountingMonth() {
        if (accountingMonth == 1) { accountingMonth = 12; accountingYear-- } else accountingMonth--
    }
    fun nextAccountingMonth() {
        if (accountingMonth == 12) { accountingMonth = 1; accountingYear++ } else accountingMonth++
    }
    fun previousCalendarMonth() {
        if (calendarMonth == 1) { calendarMonth = 12; calendarYear-- } else calendarMonth--
        selectedCalendarDay = 1
    }
    fun nextCalendarMonth() {
        if (calendarMonth == 12) { calendarMonth = 1; calendarYear++ } else calendarMonth++
        selectedCalendarDay = 1
    }

    fun setStatus(truckId: Long, status: CargoStatus) = repo.write { setTruckStatus(truckId, status) }

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.length < 6) { authMessage = "ایمیل و رمز حداقل ۶ کاراکتری را وارد کن"; return }
        viewModelScope.launch {
            authBusy = true; authMessage = ""
            try {
                val auth = withContext(Dispatchers.IO) { repo.cloud.signIn(email, password) }
                isLoggedIn = auth.signedIn
                accountEmail = repo.cloud.session.email
                authMessage = auth.message
                if (auth.signedIn) {
                    syncBusy = true
                    val r = withContext(Dispatchers.IO) { repo.cloud.bootstrap() }
                    if (r.restored) repo.changed()
                    syncMessage = r.message
                    syncBusy = false
                }
            } catch (t: Throwable) { authMessage = t.message ?: "ورود انجام نشد" }
            authBusy = false
        }
    }

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.length < 6) { authMessage = "ایمیل و رمز حداقل ۶ کاراکتری را وارد کن"; return }
        viewModelScope.launch {
            authBusy = true; authMessage = ""
            try {
                val auth = withContext(Dispatchers.IO) { repo.cloud.signUp(email, password) }
                isLoggedIn = auth.signedIn
                accountEmail = repo.cloud.session.email.ifBlank { email.trim() }
                authMessage = auth.message
                if (auth.signedIn) {
                    val r = withContext(Dispatchers.IO) { repo.cloud.bootstrap() }
                    if (r.restored) repo.changed()
                    syncMessage = r.message
                }
            } catch (t: Throwable) { authMessage = t.message ?: "ساخت حساب انجام نشد" }
            authBusy = false
        }
    }

    fun syncNow() {
        if (!isLoggedIn) return
        viewModelScope.launch {
            syncBusy = true
            val r = withContext(Dispatchers.IO) { repo.cloud.syncNow() }
            syncMessage = r.message
            syncBusy = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { repo.cloud.logout() }
            isLoggedIn = false
            accountEmail = ""
            authMessage = ""
            syncMessage = ""
            home()
        }
    }

}
