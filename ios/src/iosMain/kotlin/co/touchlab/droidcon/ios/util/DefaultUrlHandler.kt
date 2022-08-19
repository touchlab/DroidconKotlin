package co.touchlab.droidcon.ios.util

import co.touchlab.droidcon.util.UrlHandler
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

class DefaultUrlHandler : UrlHandler {

    override fun openUrl(url: String) {
        UIApplication.sharedApplication.openURL(NSURL(string = url))
    }
}
