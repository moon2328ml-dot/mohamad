package ir.rira.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WalletScreen(state: DemoState, onBack: () -> Unit, onUnlock: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())) {
        Header("صندوق من", "کلید صندوق و وضعیت برداشت", onBack)
        Column(Modifier.padding(18.dp)) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier.fillMaxWidth().height(190.dp)
                        .background(Brush.verticalGradient(listOf(EmeraldDark, NavyDark)))
                ) {
                    Icon(
                        Icons.Rounded.Key,
                        null,
                        tint = Gold,
                        modifier = Modifier.align(Alignment.TopStart).padding(20.dp).size(58.dp)
                    )
                    Column(
                        Modifier.align(Alignment.CenterEnd).padding(20.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text("صندوق این ماه", color = Color.White.copy(.72f))
                        Text(
                            toman(state.monthlyToman * state.seats),
                            color = GoldSoft,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            when {
                                state.vaultUnlocked -> "صندوق باز است؛ برداشت فعال"
                                state.received -> "کلید به شما رسیده؛ در حال بررسی بانکی"
                                else -> "کلید صندوق هنوز به شما نرسیده"
                            },
                            color = Color.White
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            VaultStep("۱", "قرعه یا ماه رزروشده", state.received)
            VaultStep("۲", "بررسی پرداخت‌ها و تعهدات", state.received)
            VaultStep("۳", "تأیید بانکی", state.vaultUnlocked)
            VaultStep("۴", "باز شدن صندوق و برداشت", state.vaultUnlocked)
            Spacer(Modifier.height(20.dp))
            RiraButton(
                if (state.vaultUnlocked) "برداشت آزمایشی وجه" else "تأیید بانکی و باز کردن صندوق",
                onUnlock,
                enabled = state.received
            )
            if (!state.received) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "برای تست، ابتدا از صفحه اتاقک «اجرای قرعه آزمایشی» را بزنید.",
                    color = Muted,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun VaultStep(number: String, title: String, done: Boolean) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(32.dp).background(
                if (done) Emerald else Color(0xFFE1E7E4),
                RoundedCornerShape(10.dp)
            ),
            contentAlignment = Alignment.Center
        ) {
            Text(if (done) "✓" else number.toPersianDigits(), color = if (done) Color.White else Muted)
        }
        Spacer(Modifier.width(10.dp))
        Text(title, color = if (done) NavyDark else Muted, fontWeight = if (done) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun ProfileScreen(state: DemoState, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())) {
        Header("پروفایل من", "اطلاعات، قراردادها و اعتبار", onBack)
        Column(Modifier.padding(18.dp)) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(58.dp).background(Color(0xFFEAF7F1), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Person, null, tint = Emerald, modifier = Modifier.size(34.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("کاربر آزمایشی ریرا", fontWeight = FontWeight.Black, fontSize = 19.sp, color = NavyDark)
                        Text("احراز هویت: تأیید شده (Demo)", color = Emerald, fontSize = 12.sp)
                    }
                    CreditBadge(state.creditTier)
                }
            }
            Spacer(Modifier.height(14.dp))
            ProfileItem(Icons.Rounded.VerifiedUser, "رتبه اعتباری", "سطح " + state.creditTier + " • امتیاز " + state.creditScore.toString().toPersianDigits())
            ProfileItem(Icons.Rounded.Description, "تعهدنامه عمومی ریرا", "تأیید الکترونیکی آزمایشی")
            ProfileItem(Icons.Rounded.Handshake, "قرارداد " + state.roomName, "پرداخت ماهانه تا پایان دوره")
            ProfileItem(Icons.Rounded.Shield, "ضمانت", "نسخه واقعی پس از اتصال سرویس اعتبارسنجی")
            ProfileItem(Icons.Rounded.ReceiptLong, "تاریخچه مالی", state.paidMonths.toString().toPersianDigits() + " پرداخت ثبت شده")
            Spacer(Modifier.height(18.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7E5)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(
                    "نسخه آزمایشی ریرا: هیچ پرداخت، وام، ضمانت یا انتقال وجه واقعی در این نسخه انجام نمی‌شود.",
                    modifier = Modifier.padding(16.dp),
                    color = Navy
                )
            }
        }
    }
}

@Composable
private fun ProfileItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Gold)
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = NavyDark, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Muted, fontSize = 12.sp)
            }
            Icon(Icons.Rounded.ChevronLeft, null, tint = Muted)
        }
    }
}
