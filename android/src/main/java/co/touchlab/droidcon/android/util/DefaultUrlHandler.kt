package co.touchlab.droidcon.android.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import co.touchlab.droidcon.util.UrlHandler

class DefaultUrlHandler(
    private val context: Context,
): UrlHandler {

    override fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
