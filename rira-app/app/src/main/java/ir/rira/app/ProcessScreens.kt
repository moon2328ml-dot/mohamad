package ir.rira.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ProcessScreen(
    title: String,
    subtitle: String,
    steps: List<String>,
    finalMessage: String,
    onDone: () -> Unit
) {
    var completed by remember(title) { mutableIntStateOf(0) }
    var finished by remember(title) { mutableStateOf(false) }

    LaunchedEffect(title) {
        completed = 0
        finished = false
        delay(550)
        for (i in steps.indices) {
            delay(900)
            completed = i + 1
        }
        delay(650)
        finished = true
        delay(1250)
        onDone()
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.White, Bg, Color(0xFFEAF5F0))
                )
            )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Box(
                Modifier
                    .size(86.dp)
                    .background(
                        if (finished) Emerald else NavyDark,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (finished) {
                    Icon(
                        Icons.Rounded.Verified,
                        contentDescription = null,
                        tint = GoldSoft,
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    CircularProgressIndicator(
                        color = GoldSoft,
                        trackColor = Color.White.copy(alpha = .16f),
                        strokeWidth = 5.dp,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(Modifier.height(22.dp))
            Text(
                title,
                color = NavyDark,
                fontWeight = FontWeight.Black,
                fontSize = 25.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(7.dp))
            Text(
                subtitle,
                color = Muted,
                fontSize = 13.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(horizontal = 18.dp, vertical = 12.dp)) {
                    steps.forEachIndexed { index, step ->
                        val done = index < completed
                        val active = index == completed && !finished

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(40.dp)
                                    .background(
                                        when {
                                            done -> Emerald
                                            active -> Color(0xFFFFF4D8)
                                            else -> Color(0xFFF0F3F2)
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    done -> Icon(
                                        Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    active -> Icon(
                                        Icons.Rounded.HourglassTop,
                                        contentDescription = null,
                                        tint = Gold,
                                        modifier = Modifier.size(21.dp)
                                    )
                                    else -> Text(
                                        (index + 1).toString().toPersianDigits(),
                                        color = Muted,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    step,
                                    color = if (done || active) NavyDark else Muted,
                                    fontWeight = if (done || active) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    when {
                                        done -> "انجام شد"
                                        active -> "در حال بررسی..."
                                        else -> "در انتظار"
                                    },
                                    color = when {
                                        done -> Emerald
                                        active -> Gold
                                        else -> Muted.copy(alpha = .75f)
                                    },
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (index < steps.lastIndex) {
                            HorizontalDivider(
                                color = Color(0xFFE9EEEC),
                                thickness = 1.dp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            LinearProgressIndicator(
                progress = { if (steps.isEmpty()) 1f else completed.toFloat() / steps.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp),
                color = Emerald,
                trackColor = Color(0xFFE2EAE6)
            )

            Spacer(Modifier.height(16.dp))

            if (finished) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE6F6EF))
                ) {
                    Row(
                        Modifier.padding(17.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.Verified,
                            contentDescription = null,
                            tint = Emerald,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            finalMessage,
                            color = NavyDark,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                Text(
                    "لطفاً چند لحظه صبر کنید...",
                    color = Muted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                "نسخه آزمایشی ریرا • این فرایند برای نمایش تجربه کاربری شبیه‌سازی شده است",
                color = Muted.copy(alpha = .75f),
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
