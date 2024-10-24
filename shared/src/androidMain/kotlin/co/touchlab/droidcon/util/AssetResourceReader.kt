package co.touchlab.droidcon.util

import android.content.Context
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import java.io.InputStreamReader

class AssetResourceReader(
    private val context: Context,
) : ResourceReader {
    override fun readResource(name: String): String {
        // TODO: Catch Android-only exceptions and map them to common ones.
        return context.assets.open(name).use { stream ->
            InputStreamReader(stream).use { reader ->
                reader.readText()
            }
        }
    }
}
