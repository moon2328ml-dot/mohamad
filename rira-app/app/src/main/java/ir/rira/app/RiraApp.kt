package ir.rira.app

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

enum class Screen {
    WELCOME,
    KYC,
    VERIFYING,
    CREDIT,
    MEMBERSHIP,
    PAYMENT,
    MATCHING,
    HOME,
    ROOM,
    DRAW,
    PRIORITY,
    WALLET,
    BANK,
    PROFILE
}

data class DemoState(
    val creditTier: String = "A",
    val creditScore: Int = 865,
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

            Screen.KYC -> KycScreen(onDone = { screen = Screen.VERIFYING })

            Screen.VERIFYING -> ProcessScreen(
                title = "احراز هویت ریرا",
                subtitle = "برای نسخه آزمایشی، مراحل به‌صورت شبیه‌سازی‌شده انجام می‌شوند.",
                steps = listOf(
                    "بررسی شماره موبایل",
                    "تطبیق کد ملی",
                    "بررسی اطلاعات هویتی",
                    "تأیید نهایی حساب"
                ),
                finalMessage = "احراز هویت با موفقیت انجام شد",
                onDone = { screen = Screen.CREDIT }
            )

            Screen.CREDIT -> CreditScreen(state) { screen = Screen.MEMBERSHIP }

            Screen.MEMBERSHIP -> MembershipScreen(state) { amount ->
                state = state.copy(monthlyToman = amount)
                screen = Screen.PAYMENT
            }

            Screen.PAYMENT -> ProcessScreen(
                title = "پرداخت حق عضویت",
                subtitle = "درگاه بانکی در نسخه آزمایشی شبیه‌سازی شده است.",
                steps = listOf(
                    "اتصال امن به درگاه",
                    "بررسی اطلاعات پرداخت",
                    "ثبت تراکنش آزمایشی",
                    "صدور رسید ریرا"
                ),
                finalMessage = "پرداخت آزمایشی با موفقیت ثبت شد",
                onDone = { screen = Screen.MATCHING }
            )

            Screen.MATCHING -> ProcessScreen(
                title = "در حال پیدا کردن اتاقک شما",
                subtitle = "سیستم خودش مناسب‌ترین اتاقک را بر اساس رتبه A انتخاب می‌کند.",
                steps = listOf(
                    "بررسی رتبه اعتباری A",
                    "جست‌وجوی اتاقک هم‌سطح",
                    "کنترل ظرفیت ۱۰ تا ۱۲ صندلی",
                    "رزرو صندلی شماره ۷"
                ),
                finalMessage = "اتاقک زرین به شما اختصاص داده شد",
                onDone = { screen = Screen.HOME }
            )

            Screen.HOME -> HomeScreen(
                state = state,
                onRoom = { screen = Screen.ROOM },
                onWallet = { screen = Screen.WALLET },
                onProfile = { screen = Screen.PROFILE }
            )

            Screen.ROOM -> RoomScreen(
                state = state,
                onBack = { screen = Screen.HOME },
                onBuyPriority = { screen = Screen.PRIORITY },
                onDraw = { screen = Screen.DRAW }
            )

            Screen.DRAW -> ProcessScreen(
                title = "قرعه‌کشی ماهانه",
                subtitle = "این قرعه صرفاً برای نمایش روند نسخه آزمایشی است.",
                steps = listOf(
                    "قفل شدن لیست اعضای واجد شرایط",
                    "حذف دریافت‌کنندگان ماه‌های قبل",
                    "ترکیب تصادفی صندلی‌ها",
                    "انتخاب صندلی برنده"
                ),
                finalMessage = "صندلی ۷ انتخاب شد؛ کلید صندوق به شما رسید",
                onDone = {
                    state = state.copy(received = true)
                    screen = Screen.WALLET
                }
            )

            Screen.PRIORITY -> ProcessScreen(
                title = "رزرو امتیاز ماه خاص",
                subtitle = "فرآیند خرید امتیاز در نسخه آزمایشی شبیه‌سازی می‌شود.",
                steps = listOf(
                    "بررسی آزاد بودن ماه ششم",
                    "محاسبه امتیاز موردنیاز",
                    "پرداخت آزمایشی امتیاز",
                    "قفل کردن ماه ششم برای شما"
                ),
                finalMessage = "ماه ششم با موفقیت برای شما رزرو شد",
                onDone = {
                    state = state.copy(priorityMonth = 6)
                    screen = Screen.ROOM
                }
            )

            Screen.WALLET -> WalletScreen(
                state = state,
                onBack = { screen = Screen.HOME },
                onUnlock = { screen = Screen.BANK }
            )

            Screen.BANK -> ProcessScreen(
                title = "باز کردن صندوق",
                subtitle = "مراحل بانکی در این نسخه واقعی نیستند و فقط نمایش داده می‌شوند.",
                steps = listOf(
                    "کنترل پرداخت تمام اقساط سررسیدشده",
                    "بررسی تعهدنامه و قرارداد",
                    "ثبت درخواست برداشت",
                    "تأیید آزمایشی بانکی",
                    "تحویل کلید و باز شدن صندوق"
                ),
                finalMessage = "صندوق باز شد؛ برداشت آزمایشی فعال است",
                onDone = {
                    state = state.copy(vaultUnlocked = true)
                    screen = Screen.WALLET
                }
            )

            Screen.PROFILE -> ProfileScreen(state) { screen = Screen.HOME }
        }
    }
}

fun toman(v: Long): String = "%,d تومان".format(v).toPersianDigits()

fun String.toPersianDigits(): String {
    val e = "0123456789"
    val f = "۰۱۲۳۴۵۶۷۸۹"
    return map { c -> e.indexOf(c).let { if (it >= 0) f[it] else c } }.joinToString("")
}
