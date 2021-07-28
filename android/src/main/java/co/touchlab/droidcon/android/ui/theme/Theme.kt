package co.touchlab.droidcon.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Colors.teal,
    primaryVariant = Colors.darkBlue,
    secondary = Colors.lightYellow,
    surface = Color.White,
    background = Color.White,
    error = Colors.orange,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onSurface = Color.Black,
    onBackground = Color.Black,
)

private val LightColorPalette = lightColors(
    primary = Colors.teal,
    primaryVariant = Colors.darkBlue,
    secondary = Colors.lightYellow,
    surface = Color.White,
    background = Color.White,
    error = Colors.orange,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onSurface = Color.Black,
    onBackground = Color.Black,
)

@Composable
fun DroidconTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
