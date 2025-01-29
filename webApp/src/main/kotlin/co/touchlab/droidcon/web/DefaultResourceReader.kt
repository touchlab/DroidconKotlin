package co.touchlab.droidcon.web
class DefaultResourceReader { // : ResourceReader {
    fun readResource(name: String): String {
        val fs = js("require('fs')")
        return fs.readFileSync(name, "utf8") as? String ?: ""
    }
}
