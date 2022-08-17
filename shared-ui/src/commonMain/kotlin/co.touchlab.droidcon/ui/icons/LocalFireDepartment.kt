package co.touchlab.droidcon.ui.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

public val Icons.Filled.LocalFireDepartment: ImageVector
    get() {
        if (_localFireDepartment != null) {
            return _localFireDepartment!!
        }
        _localFireDepartment = materialIcon(name = "Filled.LocalFireDepartment") {
            materialPath {
                moveTo(19.48f, 12.35f)
                curveToRelative(-1.57f, -4.08f, -7.16f, -4.3f, -5.81f, -10.23f)
                curveToRelative(0.1f, -0.44f, -0.37f, -0.78f, -0.75f, -0.55f)
                curveTo(9.29f, 3.71f, 6.68f, 8.0f, 8.87f, 13.62f)
                curveToRelative(0.18f, 0.46f, -0.36f, 0.89f, -0.75f, 0.59f)
                curveToRelative(-1.81f, -1.37f, -2.0f, -3.34f, -1.84f, -4.75f)
                curveToRelative(0.06f, -0.52f, -0.62f, -0.77f, -0.91f, -0.34f)
                curveTo(4.69f, 10.16f, 4.0f, 11.84f, 4.0f, 14.37f)
                curveToRelative(0.38f, 5.6f, 5.11f, 7.32f, 6.81f, 7.54f)
                curveToRelative(2.43f, 0.31f, 5.06f, -0.14f, 6.95f, -1.87f)
                curveTo(19.84f, 18.11f, 20.6f, 15.03f, 19.48f, 12.35f)
                close()
                moveTo(10.2f, 17.38f)
                curveToRelative(1.44f, -0.35f, 2.18f, -1.39f, 2.38f, -2.31f)
                curveToRelative(0.33f, -1.43f, -0.96f, -2.83f, -0.09f, -5.09f)
                curveToRelative(0.33f, 1.87f, 3.27f, 3.04f, 3.27f, 5.08f)
                curveTo(15.84f, 17.59f, 13.1f, 19.76f, 10.2f, 17.38f)
                close()
            }
        }
        return _localFireDepartment!!
    }

private var _localFireDepartment: ImageVector? = null
