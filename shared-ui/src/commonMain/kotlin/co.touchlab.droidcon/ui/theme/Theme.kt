package co.touchlab.droidcon.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Colors.primary,
)

private val DarkColorScheme = darkColorScheme(
    primary = Colors.primary,
)

@Suppress("ktlint:standard:backing-property-naming")
private val _LightColorScheme = lightColorScheme(
    primary = Colors.primary,
    onPrimary = Colors.droidcon_theme_light_onPrimary,
    primaryContainer = Colors.droidcon_theme_light_primaryContainer,
    onPrimaryContainer = Colors.droidcon_theme_light_onPrimaryContainer,
    secondary = Colors.droidcon_theme_light_secondary,
    onSecondary = Colors.droidcon_theme_light_onSecondary,
    secondaryContainer = Colors.droidcon_theme_light_secondaryContainer,
    onSecondaryContainer = Colors.droidcon_theme_light_onSecondaryContainer,
    tertiary = Colors.droidcon_theme_light_tertiary,
    onTertiary = Colors.droidcon_theme_light_onTertiary,
    tertiaryContainer = Colors.droidcon_theme_light_tertiaryContainer,
    onTertiaryContainer = Colors.droidcon_theme_light_onTertiaryContainer,
    error = Colors.droidcon_theme_light_error,
    errorContainer = Colors.droidcon_theme_light_errorContainer,
    onError = Colors.droidcon_theme_light_onError,
    onErrorContainer = Colors.droidcon_theme_light_onErrorContainer,
    background = Colors.droidcon_theme_light_background,
    onBackground = Colors.droidcon_theme_light_onBackground,
    outline = Colors.droidcon_theme_light_outline,
    inverseOnSurface = Colors.droidcon_theme_light_inverseOnSurface,
    inverseSurface = Colors.droidcon_theme_light_inverseSurface,
    inversePrimary = Colors.droidcon_theme_light_inversePrimary,
    surfaceTint = Colors.droidcon_theme_light_surfaceTint,
    outlineVariant = Colors.droidcon_theme_light_outlineVariant,
    scrim = Colors.droidcon_theme_light_scrim,
    surface = Colors.droidcon_theme_light_surface,
    onSurface = Colors.droidcon_theme_light_onSurface,
    surfaceVariant = Colors.droidcon_theme_light_surfaceVariant,
    onSurfaceVariant = Colors.droidcon_theme_light_onSurfaceVariant,
)

@Suppress("ktlint:standard:backing-property-naming")
private val _DarkColorScheme = darkColorScheme(
    primary = Colors.droidcon_theme_dark_primary,
    onPrimary = Colors.droidcon_theme_dark_onPrimary,
    primaryContainer = Colors.droidcon_theme_dark_primaryContainer,
    onPrimaryContainer = Colors.droidcon_theme_dark_onPrimaryContainer,
    secondary = Colors.droidcon_theme_dark_secondary,
    onSecondary = Colors.droidcon_theme_dark_onSecondary,
    secondaryContainer = Colors.droidcon_theme_dark_secondaryContainer,
    onSecondaryContainer = Colors.droidcon_theme_dark_onSecondaryContainer,
    tertiary = Colors.droidcon_theme_dark_tertiary,
    onTertiary = Colors.droidcon_theme_dark_onTertiary,
    tertiaryContainer = Colors.droidcon_theme_dark_tertiaryContainer,
    onTertiaryContainer = Colors.droidcon_theme_dark_onTertiaryContainer,
    error = Colors.droidcon_theme_dark_error,
    errorContainer = Colors.droidcon_theme_dark_errorContainer,
    onError = Colors.droidcon_theme_dark_onError,
    onErrorContainer = Colors.droidcon_theme_dark_onErrorContainer,
    background = Colors.droidcon_theme_dark_background,
    onBackground = Colors.droidcon_theme_dark_onBackground,
    outline = Colors.droidcon_theme_dark_outline,
    inverseOnSurface = Colors.droidcon_theme_dark_inverseOnSurface,
    inverseSurface = Colors.droidcon_theme_dark_inverseSurface,
    inversePrimary = Colors.droidcon_theme_dark_inversePrimary,
    surfaceTint = Colors.droidcon_theme_dark_surfaceTint,
    outlineVariant = Colors.droidcon_theme_dark_outlineVariant,
    scrim = Colors.droidcon_theme_dark_scrim,
    surface = Colors.droidcon_theme_dark_surface,
    onSurface = Colors.droidcon_theme_dark_onSurface,
    surfaceVariant = Colors.droidcon_theme_dark_surfaceVariant,
    onSurfaceVariant = Colors.droidcon_theme_dark_onSurfaceVariant,
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
