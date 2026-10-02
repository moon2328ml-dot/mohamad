package ir.bordermanager.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AccountGateScreen(vm: AppViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var createMode by remember { mutableStateOf(false) }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(Modifier.fillMaxSize().padding(22.dp), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.CloudDone, null, tint = Purple, modifier = Modifier.size(52.dp))
                    Text("BorderManager", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(
                        if (createMode) "ساخت حساب شخصی برای نگهداری اطلاعات" else "ورود به حساب شخصی",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .65f)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it.trim() },
                        label = { Text("ایمیل") },
                        leadingIcon = { Icon(Icons.Default.Person, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("رمز عبور") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (vm.authMessage.isNotBlank()) {
                        Text(vm.authMessage, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                    Button(
                        onClick = {
                            if (createMode) vm.signUp(email, password) else vm.signIn(email, password)
                        },
                        enabled = !vm.authBusy,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        if (vm.authBusy) {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else Text(if (createMode) "ساخت حساب" else "ورود")
                    }
                    TextButton(onClick = { createMode = !createMode; }) {
                        Text(if (createMode) "حساب دارم؛ ورود" else "اولین باره؟ ساخت حساب")
                    }
                    Text(
                        "اطلاعات اصلی روی گوشی هم می‌ماند و بعد از ورود با حساب، نسخه ابری همگام می‌شود.",
                        fontSize = 11.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f)
                    )
                }
            }
        }
    }
}
