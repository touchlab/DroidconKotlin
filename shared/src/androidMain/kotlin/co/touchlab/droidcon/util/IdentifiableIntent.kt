package co.touchlab.droidcon.util

import android.content.Context
import android.content.Intent

class IdentifiableIntent(
    private val id: String,
    packageContext: Context,
    cls: Class<*>,
) : Intent(packageContext, cls) {

    override fun filterEquals(other: Intent?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (this.id != (other as IdentifiableIntent).id) return false
        return true
    }
}
