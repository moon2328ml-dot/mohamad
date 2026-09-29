package ir.rira.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(34.dp))
                    .background(Color(0xFFFFFCF6))
                    .border(
                        1.dp,
                        Gold.copy(alpha = .26f),
                        RoundedCornerShape(34.dp)
                    )
            ) {
                Image(
                    painter = painterResource(R.drawable.rira_brand_icon_hq),
                    contentDescription = "آیکون ریرا",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(3.dp),
                    contentScale = ContentScale.Fit
                )

                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = .45f),
                                    Color.White.copy(alpha = .92f)
                                )
                            )
                        )
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                "ریرا",
                color = NavyDark,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "یک چرخه، یک جمع، یک آینده",
                color = Gold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Shield, null, tint = Emerald, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "شفاف • ایرانی • مبتنی بر اعتبار",
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(15.dp))
            RiraButton("شروع کنیم", onStart)

            TextButton(onClick = onDemo) {
                Text(
                    "ورود مستقیم به نسخه نمایشی",
                    color = Navy,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun KycScreen(onDone: () -> Unit) {
    var mobile by remember { mutableStateOf("") }
    var nationalId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
    ) {
        Header(
            "ثبت‌نام و احراز هویت",
            "اول حساب ریرا را می‌سازیم، بعد اعتبارسنجی انجام می‌شود"
        )

        Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Step("اطلاعات فردی", true)
                        Step("احراز هویت", false)
                        Step("اعتبارسنجی", false)
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "اطلاعات اولیه حساب",
                        color = GoldSoft,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                    Text(
                        "در نسخه اصلی، اطلاعات از سرویس‌های احراز هویت معتبر بررسی می‌شوند.",
                        color = Color.White.copy(alpha = .75f),
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("شماره موبایل") },
                placeholder = { Text("۰۹۱۲ ۳۴۵ ۶۷۸۹") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = nationalId,
                onValueChange = { nationalId = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("کد ملی") },
                placeholder = { Text("کد ملی ۱۰ رقمی") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("ایمیل") },
                placeholder = { Text("example@rira.ir") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(Modifier.height(18.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF7F1)),
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.VerifiedUser,
                        contentDescription = null,
                        tint = Emerald
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "احراز هویت آزمایشی",
                            color = NavyDark,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "با زدن ادامه، مراحل احراز هویت را مرحله‌به‌مرحله می‌بینی.",
                            color = Muted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Lock, null, tint = Gold, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "هیچ اطلاعات واقعی در این نسخه ارسال یا ذخیره نمی‌شود.",
                    color = Muted,
                    fontSize = 10.sp
                )
            }

            Spacer(Modifier.height(22.dp))
            RiraButton("ادامه و شروع احراز هویت", onDone)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun CreditScreen(state: DemoState, onContinue: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.White, Bg, Color(0xFFEAF5F0))
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "اعتبارسنجی انجام شد",
            color = Emerald,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(14.dp))
        CreditBadge(state.creditTier)
        Spacer(Modifier.height(18.dp))
        Text(
            "رتبه اعتباری شما",
            fontSize = 25.sp,
            fontWeight = FontWeight.Black,
            color = NavyDark
        )
        Text(
            "امتیاز " + state.creditScore.toString().toPersianDigits(),
            color = Gold,
            fontWeight = FontWeight.Black,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column(Modifier.padding(18.dp)) {
                Text(
                    "این رتبه چه معنی دارد؟",
                    color = NavyDark,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "سیستم بر اساس رتبه اعتباری، مبلغ مشارکت و ظرفیت، اتاقک مناسب را خودش انتخاب می‌کند. شما اتاقک را دستی انتخاب نمی‌کنید.",
                    color = Muted,
                    lineHeight = 21.sp
                )
            }
        }

        Spacer(Modifier.height(28.dp))
        RiraButton("ادامه برای رزرو صندلی", onContinue)
    }
}

@Composable
fun MembershipScreen(state: DemoState, onContinue: (Long) -> Unit) {
    var amount by remember { mutableStateOf(state.monthlyToman) }
    val options = listOf(
        1_000_000L,
        5_000_000L,
        10_000_000L,
        25_000_000L,
        50_000_000L
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
    ) {
        Header(
            "رزرو صندلی",
            "اتاقک مناسب توسط سیستم انتخاب می‌شود"
        )

        Column(Modifier.padding(20.dp)) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = NavyDark)
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        "حق عضویت یک‌باره",
                        color = Color.White.copy(alpha = .72f)
                    )
                    Text(
                        "۱,۰۰۰,۰۰۰ تومان",
                        color = GoldSoft,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "برای گرفتن یک صندلی تا پایان دوره؛ این مبلغ ماهانه تکرار نمی‌شود.",
                        color = Color.White.copy(alpha = .78f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "مبلغ مشارکت ماهانه",
                color = NavyDark,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
            Text(
                "در نسخه اصلی، گزینه‌ها براساس رتبه و قوانین اتاقک نمایش داده می‌شوند.",
                color = Muted,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(12.dp))

            options.forEach { item ->
                FilterChip(
                    selected = amount == item,
                    onClick = { amount = item },
                    label = { Text(toman(item)) },
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                )
            }

            Spacer(Modifier.height(10.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "تخصیص خودکار",
                        fontWeight = FontWeight.Black,
                        color = Navy
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(
                        "رتبه " + state.creditTier +
                            " • ظرفیت ۱۰ تا ۱۲ صندلی • اعضای هم‌سطح • بدون انتخاب مستقیم اتاقک",
                        color = Muted,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            RiraButton(
                "ادامه و پرداخت حق عضویت آزمایشی",
                { onContinue(amount) }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
