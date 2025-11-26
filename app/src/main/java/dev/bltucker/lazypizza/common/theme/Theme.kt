package dev.bltucker.lazypizza.common.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    primaryContainer = Orange.copy(alpha = 0.2f),
    onPrimaryContainer = DarkBlue,
    secondary = DarkBlue,
    onSecondary = Color.White,
    background = LightGrey,
    onBackground = DarkBlue,
    surface = Color.White,
    onSurface = DarkBlue,
    surfaceVariant = LightGrey,
    onSurfaceVariant = Grey,
    error = Red,
    onError = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Orange,
    onPrimary = Color.White,
    primaryContainer = Orange.copy(alpha = 0.3f),
    onPrimaryContainer = Color.White,
    secondary = Orange.copy(alpha = 0.7f),
    onSecondary = DarkBlue,
    background = DarkBlue,
    onBackground = Color.White,
    surface = DarkBlue.copy(alpha = 0.9f),
    onSurface = Color.White,
    surfaceVariant = Grey.copy(alpha = 0.3f),
    onSurfaceVariant = LightGrey,
    error = Red,
    onError = Color.White
)

@Composable
fun LazyPizzaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
