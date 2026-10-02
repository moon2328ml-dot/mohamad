package ir.bordermanager.ui

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val Purple = Color(0xFF6D28D9)
val Purple2 = Color(0xFF7C3AED)
val PurpleSoft = Color(0xFFF3E8FF)
val Ink = Color(0xFF17134A)
val ParkingRed = Color(0xFFD92D3D)
val ParkingRedBg = Color(0xFFFFE9EC)
val WaitingGreen = Color(0xFF059669)
val WaitingGreenBg = Color(0xFFE3F8EE)

private val Light = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    primaryContainer = PurpleSoft,
    onPrimaryContainer = Ink,
    secondary = Purple2,
    background = Color(0xFFFCFBFF),
    surface = Color.White,
    onSurface = Ink,
    outline = Color(0xFFE5E2EE)
)

private val Dark = darkColorScheme(
    primary = Color(0xFFBFA5FF),
    onPrimary = Color(0xFF2E075B),
    primaryContainer = Color(0xFF3C1768),
    background = Color(0xFF111018),
    surface = Color(0xFF1B1923),
    onSurface = Color(0xFFF2EDFF),
    outline = Color(0xFF4C485A)
)

@Composable
fun BorderTheme(mode: String, content: @Composable () -> Unit) {
    val dark = when (mode) { "DARK" -> true; "SYSTEM" -> isSystemInDarkTheme(); else -> false }
    val colors = if (dark) Dark else Light
    val view = LocalView.current
    if (!view.isInEditMode) SideEffect {
        val window = (view.context as Activity).window
        window.statusBarColor = colors.background.toArgb()
        window.navigationBarColor = colors.background.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !dark
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !dark
    }
    MaterialTheme(colorScheme = colors, content = content)
}
