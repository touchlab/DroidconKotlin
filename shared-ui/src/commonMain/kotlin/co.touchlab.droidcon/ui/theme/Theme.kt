package co.touchlab.droidcon.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    background = Color.White,
    onBackground = Colors.textColor,
    surface = Color.White,
    onSurface = Colors.textColor,
    onSurfaceVariant = Colors.secondaryTextColor,
    primary = Colors.droidconGreen,
    onPrimary = Color.White,
    secondary = Colors.droidconBlue,
    onSecondary = Color.White,
    tertiary = Colors.droidconRed,
    onTertiary = Color.White,
    surfaceVariant = Color.White,
    surfaceContainer = Colors.droidconBlue,
)

private val DarkColorScheme = darkColorScheme(
    background = Color.Black,
    onBackground = Colors.darkTextColor,
    surface = Color.DarkGray,
    onSurface = Colors.darkTextColor,
    onSurfaceVariant = Colors.darkSecondaryTextColor,
    primary = Colors.droidconGreen,
    onPrimary = Color.White,
    secondary = Colors.droidconGreen,
    onSecondary = Color.White,
    tertiary = Colors.droidconRed,
    onTertiary = Color.White,
    surfaceVariant = Color.White,
    surfaceContainer = Colors.droidconGreen,
)

@Composable
internal fun DroidconTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography.typography,
        content = content,
    )
}
