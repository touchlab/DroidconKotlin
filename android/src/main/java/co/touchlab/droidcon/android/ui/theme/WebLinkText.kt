package co.touchlab.droidcon.android.ui.theme

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit

data class Hyperlink(val range: IntRange, val link: String)

@Composable
fun WebLinkText(
    text: String,
    links: List<Hyperlink>,
    modifier: Modifier = Modifier,
    normalTextColor: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    normalTextDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val uriHandler = LocalUriHandler.current
    val annotatedText = buildAnnotatedString {
        val linkStyle = SpanStyle(
            color = Colors.darkBlue,
            textDecoration = TextDecoration.Underline,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
        )
        val normalStyle = SpanStyle(
            color = normalTextColor,
            textDecoration = normalTextDecoration,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
        )

        links
            .sortedBy { it.range.first }
            .forEach { hyperlink ->
                withStyle(style = normalStyle) {
                    append(text.substring(length, hyperlink.range.first))
                }
                withStyle(style = linkStyle) {
                    append(text.substring(hyperlink.range.first, hyperlink.range.last + 1))
                    addStringAnnotation(
                        tag = "URL",
                        annotation = hyperlink.link,
                        start = hyperlink.range.first,
                        end = hyperlink.range.last + 1,
                    )
                }
            }

        withStyle(style = normalStyle) {
            append(text.substring(length))
        }
    }
    Text(
        text = annotatedText,
        modifier = modifier.pointerInput("key") {
            detectTapGestures { offsetPosition ->
                textLayoutResult?.let {
                    val position = it.getOffsetForPosition(offsetPosition)
                    annotatedText
                        .getStringAnnotations(position, position)
                        .firstOrNull()
                        ?.let { result ->
                            uriHandler.openUri(result.item)
                        }
                }
            }
        },
        letterSpacing = letterSpacing,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        onTextLayout = {
            onTextLayout(it)
            textLayoutResult = it
        },
        style = style,
    )
}