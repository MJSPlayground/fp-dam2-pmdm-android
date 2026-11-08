package es.mjsplayground.pmdm.desarrollo.cuaderno.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Claro = lightColorScheme(primary = Color(0xFF2E7D32), onPrimary = Color.White)
private val Oscuro = darkColorScheme(primary = Color(0xFF81C784), onPrimary = Color(0xFF00390A))
@Composable
fun CuadernoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Oscuro else Claro, content = content)
}
