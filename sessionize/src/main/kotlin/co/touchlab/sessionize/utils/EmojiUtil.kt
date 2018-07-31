package co.touchlab.sessionize.utils

object EmojiUtil {
    var DECIMAL_VALUE_A = 97

    var EMOJI_ABC = intArrayOf(0x1F34E, // apple
            0x1F430, // bunny
            0x1F425, // chick
            0x1F436, // dog
            0x1F60E, // sunglasses
            0x1F438, // frog
            0x1F347, // grapes
            0x1F439, // hamster
            0x1F368, // ice cream
            0x1F456, // jeans
            0x1F428, // koala
            0x1F981, // lion
            0x1F42D, // mouse
            0x1F443, // nose
            0x1F419, // octopus
            0x1F43C, // panda
            0x1F31B, // quarter moon
            0x1F916, // robot
            0x1F575, // spy
            0x1F422, // turtle
            0x1F984, // unicorn
            0x1F596, // vulcan hand
            0x1F349, // watermelon
            0x1F47E, // extraterrestrial
            0x270C, // y hand
            0x1F634  // zzz smiley
    )

    fun getEmojiForUser(displayName: String): String {
        var unicode = 0x1F60A // default smiley

        if (!displayName.isNullOrEmpty()) {
            val c = displayName.toLowerCase()[0].toInt() - DECIMAL_VALUE_A
            if (c >= 0 && c < EMOJI_ABC.size) {
                unicode = EMOJI_ABC[c]
            }
        }

        return unicode.toChar().toString()
    }
}