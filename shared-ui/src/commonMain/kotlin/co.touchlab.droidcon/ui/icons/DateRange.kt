package co.touchlab.droidcon.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Filled.DateRange: ImageVector
    get() {
        if (_dateRange != null) {
            return _dateRange!!
        }
        _dateRange = materialIcon(name = "Filled.DateRange") {
            materialPath {
                moveTo(9.0f, 11.0f)
                lineTo(7.0f, 11.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                close()
                moveTo(13.0f, 11.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                close()
                moveTo(17.0f, 11.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                horizontalLineToRelative(2.0f)
                verticalLineToRelative(-2.0f)
                close()
                moveTo(19.0f, 4.0f)
                horizontalLineToRelative(-1.0f)
                lineTo(18.0f, 2.0f)
                horizontalLineToRelative(-2.0f)
                verticalLineToRelative(2.0f)
                lineTo(8.0f, 4.0f)
                lineTo(8.0f, 2.0f)
                lineTo(6.0f, 2.0f)
                verticalLineToRelative(2.0f)
                lineTo(5.0f, 4.0f)
                curveToRelative(-1.11f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
                lineTo(3.0f, 20.0f)
                curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(14.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                lineTo(21.0f, 6.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
                moveTo(19.0f, 20.0f)
                lineTo(5.0f, 20.0f)
                lineTo(5.0f, 9.0f)
                horizontalLineToRelative(14.0f)
                verticalLineToRelative(11.0f)
                close()
            }
        }
        return _dateRange!!
    }

private var _dateRange: ImageVector? = null
