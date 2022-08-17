package co.touchlab.droidcon.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Filled.MailOutline: ImageVector
    get() {
        if (_mailOutline != null) {
            return _mailOutline!!
        }
        _mailOutline = materialIcon(name = "Filled.MailOutline") {
            materialPath {
                moveTo(20.0f, 4.0f)
                lineTo(4.0f, 4.0f)
                curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
                lineTo(2.0f, 18.0f)
                curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                horizontalLineToRelative(16.0f)
                curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                lineTo(22.0f, 6.0f)
                curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                close()
                moveTo(20.0f, 18.0f)
                lineTo(4.0f, 18.0f)
                lineTo(4.0f, 8.0f)
                lineToRelative(8.0f, 5.0f)
                lineToRelative(8.0f, -5.0f)
                verticalLineToRelative(10.0f)
                close()
                moveTo(12.0f, 11.0f)
                lineTo(4.0f, 6.0f)
                horizontalLineToRelative(16.0f)
                lineToRelative(-8.0f, 5.0f)
                close()
            }
        }
        return _mailOutline!!
    }

private var _mailOutline: ImageVector? = null
