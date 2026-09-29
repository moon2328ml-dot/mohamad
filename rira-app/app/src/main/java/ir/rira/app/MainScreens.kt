package ir.rira.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    state: DemoState,
    onRoom: () -> Unit,
    onWallet: () -> Unit,
    onProfile: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())) {
        Header("داشبورد ریرا", "سلام، خوش اومدی")
        Column(Modifier.padding(horizontal = 18.dp)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EmeraldDark),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    CreditBadge(state.creditTier)
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("رتبه اعتباری شما", color = Color.White.copy(.75f))
                        Text("وضعیت بسیار خوب", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Text("امتیاز " + state.creditScore.toString().toPersianDigits(), color = GoldSoft)
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                InfoTile("اتاقک فعال", state.roomName, Icons.Rounded.Groups, Modifier.weight(1f))
                InfoTile("قسط این ماه", toman(state.monthlyToman), Icons.Rounded.Payments, Modifier.weight(1f))
                InfoTile("ماه دوره", state.currentMonth.toString().toPersianDigits() + " از ۱۲", Icons.Rounded.CalendarMonth, Modifier.weight(1f))
            }
            Spacer(Modifier.height(14.dp))
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                modifier = Modifier.fillMaxWidth().clickable(onClick = onWallet)
            ) {
                Box(
                    Modifier.fillMaxWidth().height(128.dp)
                        .background(Brush.horizontalGradient(listOf(EmeraldDark, NavyDark)))
                ) {
                    Icon(Icons.Rounded.Key, null, tint = Gold, modifier = Modifier.align(Alignment.CenterStart).padding(22.dp).size(54.dp))
                    Column(Modifier.align(Alignment.CenterEnd).padding(18.dp), horizontalAlignment = Alignment.End) {
                        Text("کلید صندوق این ماه", color = GoldSoft, fontWeight = FontWeight.Black, fontSize = 23.sp)
                        Text(
                            if (state.received) "کلید به شما رسیده؛ وضعیت برداشت را ببینید"
                            else "با مشارکت، فرصت دریافت صندوق ادامه دارد",
                            color = Color.White.copy(.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Text("اتاقک‌های من", fontWeight = FontWeight.Black, color = NavyDark, fontSize = 20.sp)
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onRoom),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(56.dp).background(Color(0xFFFFF4D8), RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.AccountBalance, null, tint = Gold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(state.roomName, fontWeight = FontWeight.Black, color = NavyDark)
                            Spacer(Modifier.width(8.dp))
                            SuggestionChip(onClick = {}, label = { Text("فعال") })
                        }
                        Text("صندلی " + state.seat.toString().toPersianDigits() + " از " + state.seats.toString().toPersianDigits(), color = Muted)
                        Text("ماهانه: " + toman(state.monthlyToman), color = Emerald, fontSize = 12.sp)
                    }
                    Icon(Icons.Rounded.ChevronLeft, null, tint = Muted)
                }
            }
            Spacer(Modifier.height(14.dp))
            OutlinedButton(onClick = onProfile, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Rounded.Person, null)
                Spacer(Modifier.width(8.dp))
                Text("پروفایل، قراردادها و ضمانت‌ها")
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
fun RoomScreen(
    state: DemoState,
    onBack: () -> Unit,
    onBuyPriority: () -> Unit,
    onDraw: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())) {
        Header("جزئیات اتاقک", state.roomName, onBack)
        Column(Modifier.padding(horizontal = 18.dp)) {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    Modifier.fillMaxWidth().height(122.dp)
                        .background(Brush.horizontalGradient(listOf(EmeraldDark, NavyDark)))
                ) {
                    Text("𐎠", color = Color.White.copy(.09f), fontSize = 78.sp, modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp))
                    Icon(Icons.Rounded.WorkspacePremium, null, tint = Gold, modifier = Modifier.align(Alignment.TopEnd).padding(14.dp))
                    Column(Modifier.align(Alignment.CenterEnd).padding(16.dp), horizontalAlignment = Alignment.End) {
                        Text(state.roomName, color = GoldSoft, fontWeight = FontWeight.Black, fontSize = 25.sp)
                        Text("با هم قوی‌تر؛ به سوی آینده", color = Color.White.copy(.8f))
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(state.seats.toString().toPersianDigits() + " صندلی", fontWeight = FontWeight.Bold)
                Text("صندلی شما: " + state.seat.toString().toPersianDigits(), color = Emerald, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                repeat(state.seats) { index ->
                    val n = index + 1
                    Box(
                        Modifier.size(22.dp).background(
                            when {
                                n == state.seat -> Gold
                                n <= 7 -> Emerald
                                else -> Color(0xFFDDE4E1)
                            }, CircleShape
                        )
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("مبلغ ماهانه", color = Muted)
                    Text(toman(state.monthlyToman), fontWeight = FontWeight.Black, color = NavyDark, fontSize = 22.sp)
                    Text("مبلغ صندوق این ماه: " + toman(state.monthlyToman * state.seats), color = Emerald)
                }
            }
            Spacer(Modifier.height(14.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF7F1)), shape = RoundedCornerShape(18.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.CheckCircle, null, tint = Emerald)
                    Spacer(Modifier.width(10.dp))
                    Text("پرداخت این ماه انجام شده و شما آماده قرعه هستید.", color = Navy)
                }
            }
            Spacer(Modifier.height(14.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Step("پرداخت", false, true)
                Step("قرعه", true, state.received)
                Step("بررسی بانکی", false, state.vaultUnlocked)
                Step("برداشت", false, state.vaultUnlocked)
            }
            Spacer(Modifier.height(18.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.WorkspacePremium, null, tint = Gold)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("خرید امتیاز ماه خاص", color = GoldSoft, fontWeight = FontWeight.Black)
                        Text(
                            if (state.priorityMonth == null) "برای رزرو ماه مشخص در نسخه آزمایشی امتحان کنید."
                            else "ماه ۶ برای شما رزرو شده است.",
                            color = Color.White.copy(.8f),
                            fontSize = 12.sp
                        )
                    }
                    Button(onClick = onBuyPriority, colors = ButtonDefaults.buttonColors(containerColor = Emerald)) {
                        Text("رزرو")
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onDraw, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Icon(Icons.Rounded.Casino, null)
                Spacer(Modifier.width(8.dp))
                Text(if (state.received) "قرعه این ماه انجام شد" else "اجرای قرعه آزمایشی")
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}
