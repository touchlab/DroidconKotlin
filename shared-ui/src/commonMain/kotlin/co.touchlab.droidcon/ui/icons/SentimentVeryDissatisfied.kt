package co.touchlab.droidcon.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Filled.SentimentVeryDissatisfied: ImageVector
    get() {
        if (_sentimentVeryDissatisfied != null) {
            return _sentimentVeryDissatisfied!!
        }
        _sentimentVeryDissatisfied = materialIcon(name = "Filled.SentimentVeryDissatisfied") {
            materialPath {
                moveTo(15.5f, 9.5f)
                moveToRelative(-1.5f, 0.0f)
                arcToRelative(1.5f, 1.5f, 0.0f, true, true, 3.0f, 0.0f)
                arcToRelative(1.5f, 1.5f, 0.0f, true, true, -3.0f, 0.0f)
            }
            materialPath {
                moveTo(8.5f, 9.5f)
                moveToRelative(-1.5f, 0.0f)
                arcToRelative(1.5f, 1.5f, 0.0f, true, true, 3.0f, 0.0f)
                arcToRelative(1.5f, 1.5f, 0.0f, true, true, -3.0f, 0.0f)
            }
            materialPath {
                moveTo(11.99f, 2.0f)
                curveTo(6.47f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                reflectiveCurveToRelative(4.47f, 10.0f, 9.99f, 10.0f)
                curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
                reflectiveCurveTo(17.52f, 2.0f, 11.99f, 2.0f)
                close()
                moveTo(12.0f, 20.0f)
                curveToRelative(-4.42f, 0.0f, -8.0f, -3.58f, -8.0f, -8.0f)
                reflectiveCurveToRelative(3.58f, -8.0f, 8.0f, -8.0f)
                reflectiveCurveToRelative(8.0f, 3.58f, 8.0f, 8.0f)
                reflectiveCurveToRelative(-3.58f, 8.0f, -8.0f, 8.0f)
                close()
                moveTo(12.0f, 14.0f)
                curveToRelative(-2.33f, 0.0f, -4.32f, 1.45f, -5.12f, 3.5f)
                horizontalLineToRelative(1.67f)
                curveToRelative(0.69f, -1.19f, 1.97f, -2.0f, 3.45f, -2.0f)
                reflectiveCurveToRelative(2.75f, 0.81f, 3.45f, 2.0f)
                horizontalLineToRelative(1.67f)
                curveToRelative(-0.8f, -2.05f, -2.79f, -3.5f, -5.12f, -3.5f)
                close()
            }
        }
        return _sentimentVeryDissatisfied!!
    }

private var _sentimentVeryDissatisfied: ImageVector? = null
