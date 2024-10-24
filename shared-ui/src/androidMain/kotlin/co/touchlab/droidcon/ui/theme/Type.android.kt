package co.touchlab.droidcon.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import co.touchlab.droidcon.sharedui.R

internal val montserratLightFont = Font(R.font.montserrat_regular, FontWeight.Light)
internal val montserratRegularFont = Font(R.font.montserrat_regular, FontWeight.Normal)
internal val montserratMediumFont = Font(R.font.montserrat_medium, FontWeight.Medium)
internal val montserratSemiBoldFont = Font(R.font.montserrat_semi_bold, FontWeight.SemiBold)
internal val montserratBoldFont = Font(R.font.montserrat_semi_bold, FontWeight.Bold)

actual val montserratFontFamily: FontFamily = FontFamily(
    montserratLightFont,
    montserratRegularFont,
    montserratMediumFont,
    montserratSemiBoldFont,
    montserratBoldFont,
)
