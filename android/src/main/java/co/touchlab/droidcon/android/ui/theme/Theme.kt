package co.touchlab.droidcon.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Colors.berlinPrimary,
    primaryVariant = Colors.berlinPrimary,
    secondary = Colors.berlinSecondary,
    secondaryVariant = Colors.berlinSecondary,
    surface = Colors.berlinPrimary,
    background = Colors.darkGrey32,
    error = Colors.orange,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onSurface = Color.White,
    onBackground = Color.White,
)

private val LightColorPalette = lightColors(
    primary = Colors.berlinPrimary,
    primaryVariant = Colors.berlinPrimary,
    secondary = Colors.berlinSecondary,
    // color of the switch (checked)
    secondaryVariant = Colors.berlinSecondary,
    // color of the switch (unchecked)
    surface = Colors.lightGrey220,
    background = Colors.lightGrey250,
    error = Colors.orange,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onSurface = Color.Black,
    onBackground = Colors.darkGrey51,
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
