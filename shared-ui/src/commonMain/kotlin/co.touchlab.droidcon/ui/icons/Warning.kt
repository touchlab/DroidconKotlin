package co.touchlab.droidcon.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Filled.Warning: ImageVector
    get() {
        if (_warning != null) {
            return _warning!!
        }
        _warning = materialIcon(name = "Filled.Warning") {
            materialPath {
                moveTo(1.0f, 21.0f)
                horizontalLineToRelative(22.0f)
                lineTo(12.0f, 2.0f)
                lineTo(1.0f, 21.0f)
                close()
                moveTo(13.0f, 18.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(2.0f)
                close()
                moveTo(13.0f, 14.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(-4.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(4.0f)
                close()
            }
        }
        return _warning!!
    }

private var _warning: ImageVector? = null
