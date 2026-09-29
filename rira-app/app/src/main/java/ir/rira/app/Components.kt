package ir.rira.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Header(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "بازگشت", tint = Navy)
            }
        }
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Black, fontSize = 22.sp, color = NavyDark)
            if (subtitle != null) Text(subtitle, color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
fun RiraButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Emerald)
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
fun InfoTile(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            Modifier.padding(13.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = Gold, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(6.dp))
            Text(title, color = Muted, fontSize = 11.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(value, color = NavyDark, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun CreditBadge(tier: String) {
    Box(
        Modifier.size(68.dp)
            .background(Brush.radialGradient(listOf(Teal, EmeraldDark)), CircleShape)
            .border(3.dp, Color.White.copy(.25f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(tier, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
fun Step(label: String, active: Boolean, done: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier.size(38.dp).background(
                if (active || done) Emerald else Color(0xFFE4EAE7), CircleShape
            ),
            contentAlignment = Alignment.Center
        ) {
            if (done) Icon(Icons.Rounded.Check, null, tint = Color.White)
            else Text("•", color = if (active) Color.White else Muted, fontSize = 20.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = if (active || done) NavyDark else Muted)
    }
}
