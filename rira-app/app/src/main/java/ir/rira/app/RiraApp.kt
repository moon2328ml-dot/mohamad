package ir.rira.app

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

enum class Screen { WELCOME, KYC, CREDIT, MEMBERSHIP, HOME, ROOM, WALLET, PROFILE }

data class DemoState(
    val creditTier: String = "A",
    val creditScore: Int = ۸۶۵,
    val roomName: String = "اتاقک زرین",
    val seat: Int = 7,
    val seats: Int = 12,
    val monthlyToman: Long = 10_000_000,
    val paidMonths: Int = 3,
    val currentMonth: Int = 4,
    val received: Boolean = false,
    val vaultUnlocked: Boolean = false,
    val priorityMonth: Int? = null
)

val Navy = Color(0xFF0E344A)
val NavyDark = Color(0xFF082536)
val Emerald = Color(0xFF087F5B)
val EmeraldDark = Color(0xFF075A45)
val Teal = Color(0xFF159A9C)
val Gold = Color(0xFFC8A45B)
val GoldSoft = Color(0xFFF2D998)
val Bg = Color(0xFFF7FAF8)
val Muted = Color(0xFF6B7773)

@Composable
fun RiraApp() {
    var screen by remember { mutableStateOf(Screen.WELCOME) }
    var state by remember { mutableStateOf(DemoState()) }

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Emerald,
            secondary = Teal,
            tertiary = Gold,
            background = Bg,
            surface = Color.White,
            onPrimary = Color.White,
            onBackground = NavyDark,
            onSurface = NavyDark
        )
    ) {
        when (screen) {
            Screen.WELCOME -> WelcomeScreen(
                onStart = { screen = Screen.KYC },
                onDemo = { screen = Screen.HOME }
            )
            Screen.KYC -> KycScreen(onDone = { screen = Screen.CREDIT })
            Screen.CREDIT -> CreditScreen(state) { screen = Screen.MEMBERSHIP }
            Screen.MEMBERSHIP -> MembershipScreen(state) { amount ->
                state = state.copy(monthlyToman = amount)
                screen = Screen.HOME
            }
            Screen.HOME -> HomeScreen(
                state = state,
                onRoom = { screen = Screen.ROOM },
                onWallet = { screen = Screen.WALLET },
                onProfile = { screen = Screen.PROFILE }
            )
            Screen.ROOM -> RoomScreen(
                state = state,
                onBack = { screen = Screen.HOME },
                onBuyPriority = {
                    state = state.copy(priorityMonth = 6)
                },
                onDraw = {
                    state = state.copy(received = true)
                }
            )
            Screen.WALLET -> WalletScreen(
                state = state,
                onBack = { screen = Screen.HOME },
                onUnlock = { state = state.copy(vaultUnlocked = true) }
            )
            Screen.PROFILE -> ProfileScreen(state) { screen = Screen.HOME }
        }
    }
}

fun toman(v: Long): String = "%,d تومان".format(v).toPersianDigits()
fun String.toPersianDigits(): String {
    val e = "0123456789"; val f = "۰۱۲۳۴۵۶۷۸۹"
    return map { c -> e.indexOf(c).let { if (it >= 0) f[it] else c } }.joinToString("")
}
