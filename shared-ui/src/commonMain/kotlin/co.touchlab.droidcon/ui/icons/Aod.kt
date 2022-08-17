package co.touchlab.droidcon.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Filled.Aod: ImageVector
    get() {
        if (_aod != null) {
            return _aod!!
        }
        _aod = materialIcon(name = "Filled.Aod") {
            materialPath {
                moveTo(17.0f, 1.01f)
                lineTo(7.0f, 1.0f)
                curveTo(5.9f, 1.0f, 5.0f, 1.9f, 5.0f, 3.0f)
                verticalLineToRelative(18.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(10.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                verticalLineTo(3.0f)
                curveTo(19.0f, 1.9f, 18.1f, 1.01f, 17.0f, 1.01f)
                close()
                moveTo(17.0f, 18.0f)
                horizontalLineTo(7.0f)
                verticalLineTo(6.0f)
                horizontalLineToRelative(10.0f)
                verticalLineTo(18.0f)
                close()
                moveTo(8.0f, 10.0f)
                horizontalLineToRelative(8.0f)
                verticalLineToRelative(1.5f)
                horizontalLineTo(8.0f)
                verticalLineTo(10.0f)
                close()
                moveTo(9.0f, 13.0f)
                horizontalLineToRelative(6.0f)
                verticalLineToRelative(1.5f)
                horizontalLineTo(9.0f)
                verticalLineTo(13.0f)
                close()
            }
        }
        return _aod!!
    }

private var _aod: ImageVector? = null
