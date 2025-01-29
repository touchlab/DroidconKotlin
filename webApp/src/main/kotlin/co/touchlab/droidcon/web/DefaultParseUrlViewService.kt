package co.touchlab.droidcon.web
class DefaultParseUrlViewService { // : ParseUrlViewService {

    private val urlRegex = """https?://(www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}\b([-a-zA-Z0-9()@:%_+.~#?&/=]*)""".toRegex()
    private val urlText = "\\b((?:https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:, .;]*[-a-zA-Z0-9+&@#/%=~_|])".toRegex()
/*
    fun parse(text: String): List<WebLink> {
        return urlRegex.findAll(text).map { result ->
            result.range
            result.value
        }.toList()
    }*/
}
