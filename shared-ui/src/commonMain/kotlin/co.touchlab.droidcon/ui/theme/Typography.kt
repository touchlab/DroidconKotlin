package co.touchlab.droidcon.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

internal object Typography {
    val typography = Typography(
        headlineLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 32.sp,
        ),
        headlineMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 28.sp,
        ),
        headlineSmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            letterSpacing = 0.15.sp,
        ),
        titleLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
        ),
        titleSmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        ),
        bodyLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
        ),
        labelLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp,
        ),
    )
}
