package ir.rira.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(onStart: () -> Unit, onDemo: () -> Unit) {
    Box(Modifier.fillMaxSize().background(Color.White)) {
        Image(
            painterResource(R.drawable.rira_brand_icon),
            "ریرا",
            Modifier.fillMaxWidth().fillMaxHeight(.72f),
            contentScale = ContentScale.Crop
        )
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Transparent, Color.White.copy(.25f), Color.White)
                )
            )
        )
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text("ریرا", color = Gold, fontSize = 44.sp, fontWeight = FontWeight.Black)
            Text(
                "ری‌را؛ یک چرخه، یک جمع، یک آینده",
                color = Navy,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            RiraButton("شروع کنیم", onStart)
            TextButton(onClick = onDemo) {
                Text("ورود به حساب آزمایشی", color = Navy, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun KycScreen(onDone: () -> Unit) {
    var mobile by remember { mutableStateOf("") }
    var nationalId by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())) {
        Header("ثبت‌نام و احراز هویت", "اطلاعات شما با بالاترین سطح امنیت نگهداری می‌شود")
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Step("اطلاعات فردی", true)
                Step("احراز هویت", false)
                Step("اعتبارسنجی", false)
            }
            Spacer(Modifier.height(28.dp))
            OutlinedTextField(
                mobile, { mobile = it },
                Modifier.fillMaxWidth(),
                label = { Text("شماره موبایل") },
                placeholder = { Text("۰۹۱۲ ۳۴۵ ۶۷۸۹") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                nationalId, { nationalId = it },
                Modifier.fillMaxWidth(),
                label = { Text("کد ملی") },
                placeholder = { Text("کد ملی ۱۰ رقمی") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                "example@rira.ir", {},
                Modifier.fillMaxWidth(),
                label = { Text("ایمیل") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(22.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F7F4)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.VerifiedUser, null, tint = Emerald)
                    Spacer(Modifier.width(10.dp))
                    Text("این نسخه آزمایشی است و احراز هویت واقعی انجام نمی‌شود.", color = Navy)
                }
            }
            Spacer(Modifier.height(24.dp))
            RiraButton("ادامه", onDone)
        }
    }
}

@Composable
fun CreditScreen(state: DemoState, onContinue: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Bg).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CreditBadge(state.creditTier)
        Spacer(Modifier.height(18.dp))
        Text("رتبه اعتباری شما", fontSize = 24.sp, fontWeight = FontWeight.Black, color = NavyDark)
        Text("امتیاز " + state.creditScore.toString().toPersianDigits(), color = Emerald, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text(
            "سیستم بر اساس رتبه اعتباری، مبلغ مشارکت و ظرفیت، اتاقک مناسب را به‌صورت خودکار به شما اختصاص می‌دهد.",
            color = Muted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(30.dp))
        RiraButton("ادامه برای دریافت صندلی", onContinue)
    }
}

@Composable
fun MembershipScreen(state: DemoState, onContinue: (Long) -> Unit) {
    var amount by remember { mutableStateOf(state.monthlyToman) }
    val options = listOf(1_000_000L, 5_000_000L, 10_000_000L, 25_000_000L, 50_000_000L)
    Column(Modifier.fillMaxSize().background(Bg).verticalScroll(rememberScrollState())) {
        Header("رزرو صندلی", "اتاقک توسط سیستم انتخاب می‌شود")
        Column(Modifier.padding(20.dp)) {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Navy)) {
                Column(Modifier.padding(18.dp)) {
                    Text("حق عضویت یک‌باره", color = Color.White.copy(.75f))
                    Text("۱,۰۰۰,۰۰۰ تومان", color = GoldSoft, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Text("این مبلغ فقط یک بار برای کل دوره دریافت می‌شود.", color = Color.White.copy(.78f), fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(18.dp))
            Text("مبلغ مشارکت ماهانه", color = NavyDark, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(10.dp))
            options.forEach { item ->
                FilterChip(
                    selected = amount == item,
                    onClick = { amount = item },
                    label = { Text(toman(item)) },
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("نحوه تخصیص", fontWeight = FontWeight.Black, color = Navy)
                    Text("رتبه " + state.creditTier + " • ظرفیت ۱۰ تا ۱۲ صندلی • بدون انتخاب مستقیم اتاقک", color = Muted)
                }
            }
            Spacer(Modifier.height(24.dp))
            RiraButton("پرداخت آزمایشی و ورود", { onContinue(amount) })
        }
    }
}
