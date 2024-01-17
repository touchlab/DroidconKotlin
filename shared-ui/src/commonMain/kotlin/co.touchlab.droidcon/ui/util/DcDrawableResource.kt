package co.touchlab.droidcon.ui.util

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ResourceItem

enum class DrawableType(val extension: String) {
    PNG(".png"),
    VECTOR(".xml")
}

@Suppress("FunctionName")
fun DcDrawableResource(name: String, type: DrawableType = DrawableType.PNG) =
    DrawableResource(
        id = "drawable:$name",
        items = setOf(
            ResourceItem(setOf(), "composeResources/drawable/$name${type.extension}")
        )
    )
