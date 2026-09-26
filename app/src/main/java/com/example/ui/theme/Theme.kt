package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SlimeDarkColorScheme = darkColorScheme(
  primary = SlimeGreen,
  onPrimary = Color.Black,
  primaryContainer = SlimeGreenLight,
  onPrimaryContainer = Color.Black,
  secondary = GooCyan,
  onSecondary = Color.Black,
  tertiary = MutationPurple,
  onTertiary = Color.White,
  background = DarkBackground,
  onBackground = Color(0xFFF8FAFC),
  surface = DarkSurface,
  onSurface = Color(0xFFF8FAFC),
  surfaceVariant = DarkBorder,
  onSurfaceVariant = Color(0xFF94A3B8)
)

@Composable
fun SlimeFactoryTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = SlimeDarkColorScheme,
    typography = Typography,
    content = content
  )
}
