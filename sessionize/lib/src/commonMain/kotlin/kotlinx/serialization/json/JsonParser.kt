/*
 * Copyright 2018 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kotlinx.serialization.json

import co.touchlab.sessionize.platform.SharedImmutable
import co.touchlab.sessionize.platform.freeze2
import kotlinx.atomicfu.atomic

internal object ParserConstants {
    // special strings
    internal const val NULL = "null"

    // special chars
    internal const val COMMA = ','
    internal const val COLON = ':'
    internal const val BEGIN_OBJ = '{'
    internal const val END_OBJ = '}'
    internal const val BEGIN_LIST = '['
    internal const val END_LIST = ']'
    internal const val STRING = '"'
    internal const val STRING_ESC = '\\'

    internal const val INVALID = 0.toChar()
    internal const val UNICODE_ESC = 'u'

    // token classes
    internal const val TC_OTHER: Byte = 0
    internal const val TC_STRING: Byte = 1
    internal const val TC_STRING_ESC: Byte = 2
    internal const val TC_WS: Byte = 3
    internal const val TC_COMMA: Byte = 4
    internal const val TC_COLON: Byte = 5
    internal const val TC_BEGIN_OBJ: Byte = 6
    internal const val TC_END_OBJ: Byte = 7
    internal const val TC_BEGIN_LIST: Byte = 8
    internal const val TC_END_LIST: Byte = 9
    internal const val TC_NULL: Byte = 10
    internal const val TC_INVALID: Byte = 11
    internal const val TC_EOF: Byte = 12
}
// mapping from chars to token classes
private const val CTC_MAX = 0x7e

@SharedImmutable
private val C2TC = ByteArray(CTC_MAX).apply {
    for (i in 0..0x20)
        initC2TC(i, ParserConstants.TC_INVALID)
    initC2TC(0x09, ParserConstants.TC_WS)
    initC2TC(0x0a, ParserConstants.TC_WS)
    initC2TC(0x0d, ParserConstants.TC_WS)
    initC2TC(0x20, ParserConstants.TC_WS)
    initC2TC(ParserConstants.COMMA, ParserConstants.TC_COMMA)
    initC2TC(ParserConstants.COLON, ParserConstants.TC_COLON)
    initC2TC(ParserConstants.BEGIN_OBJ, ParserConstants.TC_BEGIN_OBJ)
    initC2TC(ParserConstants.END_OBJ, ParserConstants.TC_END_OBJ)
    initC2TC(ParserConstants.BEGIN_LIST, ParserConstants.TC_BEGIN_LIST)
    initC2TC(ParserConstants.END_LIST, ParserConstants.TC_END_LIST)
    initC2TC(ParserConstants.STRING, ParserConstants.TC_STRING)
    initC2TC(ParserConstants.STRING_ESC, ParserConstants.TC_STRING_ESC)
}.freeze2()

private fun ByteArray.initC2TC(c: Int, cl: Byte) {
    this[c] = cl
}

private fun ByteArray.initC2TC(c: Char, cl: Byte) {
    initC2TC(c.toInt(), cl)
}

internal fun charToTokenClass(c: Char) = if (c.toInt() < CTC_MAX) C2TC[c.toInt()] else ParserConstants.TC_OTHER

// mapping from escape chars real chars
private const val C2ESC_MAX = 0x5d
private const val ESC2C_MAX = 0x75

@SharedImmutable
private val ESC2C = atomic(CharArray(ESC2C_MAX).freeze2())

@SharedImmutable
private val C2ESC = CharArray(C2ESC_MAX).apply {
    for (i in 0x00..0x1f)
        initC2ESC(i, ParserConstants.UNICODE_ESC)
    initC2ESC(0x08, 'b')
    initC2ESC(0x09, 't')
    initC2ESC(0x0a, 'n')
    initC2ESC(0x0c, 'f')
    initC2ESC(0x0d, 'r')
    initC2ESC('/', '/')
    initC2ESC(ParserConstants.STRING, ParserConstants.STRING)
    initC2ESC(ParserConstants.STRING_ESC, ParserConstants.STRING_ESC)
}.freeze2()

private fun CharArray.initC2ESC(c: Int, esc: Char) {
    this[c] = esc
    if (esc != ParserConstants.UNICODE_ESC) {
        val mod = ESC2C.value.copyOf()
        mod[esc.toInt()] = c.toChar()
        ESC2C.value = mod.freeze2()
    }
}

private fun CharArray.initC2ESC(c: Char, esc: Char) = initC2ESC(c.toInt(), esc)

internal fun escapeToChar(c: Int): Char = if (c < ESC2C_MAX) ESC2C.value[c] else ParserConstants.INVALID


// JSON low level parser
internal class Parser(val source: String) {
    var curPos: Int = 0 // position in source
        private set

    // updated by nextToken
    var tokenPos: Int = 0
        private set
    var tc: Byte = ParserConstants.TC_EOF
        private set

    // update by nextString/nextLiteral
    private var offset = -1 // when offset >= 0 string is in source, otherwise in buf
    private var length = 0 // length of string
    private var buf = CharArray(16) // only used for strings with escapes

    init {
        nextToken()
    }

    internal inline fun requireTc(expected: Byte, lazyErrorMsg: () -> String) {
        if (tc != expected)
            fail(tokenPos, lazyErrorMsg())
    }

    val canBeginValue: Boolean
        get() = when (tc) {
            ParserConstants.TC_BEGIN_LIST, ParserConstants.TC_BEGIN_OBJ, ParserConstants.TC_OTHER, ParserConstants.TC_STRING, ParserConstants.TC_NULL -> true
            else -> false
        }

    fun takeStr(): String {
        if (tc != ParserConstants.TC_OTHER && tc != ParserConstants.TC_STRING) fail(tokenPos, "Expected string or non-null literal")
        val prevStr = if (offset < 0)
            buf.createString(length) else
            source.substring(offset, offset + length)
        nextToken()
        return prevStr
    }

    private fun append(ch: Char) {
        if (length >= buf.size) buf = buf.copyOf(2 * buf.size)
        buf[length++] = ch
    }

    // initializes buf usage upon the first encountered escaped char
    private fun appendRange(source: String, fromIndex: Int, toIndex: Int) {
        val addLen = toIndex - fromIndex
        val oldLen = length
        val newLen = oldLen + addLen
        if (newLen > buf.size) buf = buf.copyOf(newLen.coerceAtLeast(2 * buf.size))
        for (i in 0 until addLen) buf[oldLen + i] = source[fromIndex + i]
        length += addLen
    }

    fun nextToken() {
        val source = source
        var curPos = curPos
        val maxLen = source.length
        while (true) {
            if (curPos >= maxLen) {
                tokenPos = curPos
                tc = ParserConstants.TC_EOF
                return
            }
            val ch = source[curPos]
            val tc = charToTokenClass(ch)
            when (tc) {
                ParserConstants.TC_WS -> curPos++ // skip whitespace
                ParserConstants.TC_OTHER -> {
                    nextLiteral(source, curPos)
                    return
                }
                ParserConstants.TC_STRING -> {
                    nextString(source, curPos)
                    return
                }
                else -> {
                    this.tokenPos = curPos
                    this.tc = tc
                    this.curPos = curPos + 1
                    return
                }
            }
        }
    }

    private fun nextLiteral(source: String, startPos: Int) {
        tokenPos = startPos
        offset = startPos
        var curPos = startPos
        val maxLen = source.length
        while (true) {
            curPos++
            if (curPos >= maxLen || charToTokenClass(source[curPos]) != ParserConstants.TC_OTHER) break
        }
        this.curPos = curPos
        length = curPos - offset
        tc = if (rangeEquals(source, offset, length, ParserConstants.NULL)) ParserConstants.TC_NULL else ParserConstants.TC_OTHER
    }

    private fun nextString(source: String, startPos: Int) {
        tokenPos = startPos
        length = 0 // in buffer
        var curPos = startPos + 1
        var lastPos = curPos
        val maxLen = source.length
        parse@ while (true) {
            if (curPos >= maxLen) fail(curPos, "Unexpected end in string")
            if (source[curPos] == ParserConstants.STRING) {
                break@parse
            } else if (source[curPos] == ParserConstants.STRING_ESC) {
                appendRange(source, lastPos, curPos)
                val newPos = appendEsc(source, curPos + 1)
                curPos = newPos
                lastPos = newPos
            } else {
                curPos++
            }
        }
        if (lastPos == startPos + 1) {
            // there was no escaped chars
            this.offset = lastPos
            this.length = curPos - lastPos
        } else {
            // some escaped chars were there
            appendRange(source, lastPos, curPos)
            this.offset = -1
        }
        this.curPos = curPos + 1
        tc = ParserConstants.TC_STRING
    }

    private fun appendEsc(source: String, startPos: Int): Int {
        var curPos = startPos
        require(curPos < source.length, curPos) { "Unexpected end after escape char" }
        val curChar = source[curPos++]
        if (curChar == ParserConstants.UNICODE_ESC) {
            curPos = appendHex(source, curPos)
        } else {
            val c = escapeToChar(curChar.toInt())
            require(c != ParserConstants.INVALID, curPos) { "Invalid escaped char '$curChar'" }
            append(c)
        }
        return curPos
    }

    private fun appendHex(source: String, startPos: Int): Int {
        var curPos = startPos
        append(
            ((fromHexChar(source, curPos++) shl 12) +
                    (fromHexChar(source, curPos++) shl 8) +
                    (fromHexChar(source, curPos++) shl 4) +
                    fromHexChar(source, curPos++)).toChar()
        )
        return curPos
    }

    fun skipElement() {
        if (tc != ParserConstants.TC_BEGIN_OBJ && tc != ParserConstants.TC_BEGIN_LIST) {
            nextToken()
            return
        }
        val tokenStack = mutableListOf<Byte>()
        do {
            when (tc) {
                ParserConstants.TC_BEGIN_LIST, ParserConstants.TC_BEGIN_OBJ -> tokenStack.add(tc)
                ParserConstants.TC_END_LIST -> {
                    if (tokenStack.last() != ParserConstants.TC_BEGIN_LIST) throw IllegalStateException("Invalid JSON at $curPos: found ] instead of }")
                    tokenStack.removeAt(tokenStack.size - 1)
                }
                ParserConstants.TC_END_OBJ -> {
                    if (tokenStack.last() != ParserConstants.TC_BEGIN_OBJ) throw IllegalStateException("Invalid JSON at $curPos: found } instead of ]")
                    tokenStack.removeAt(tokenStack.size - 1)
                }
            }
            nextToken()
        } while (tokenStack.isNotEmpty())
    }
}

// Utility functions

private fun fromHexChar(source: String, curPos: Int): Int {
    require(curPos < source.length, curPos) { "Unexpected end in unicode escape" }
    val curChar = source[curPos]
    return when (curChar) {
        in '0'..'9' -> curChar.toInt() - '0'.toInt()
        in 'a'..'f' -> curChar.toInt() - 'a'.toInt() + 10
        in 'A'..'F' -> curChar.toInt() - 'A'.toInt() + 10
        else -> throw fail(curPos, "Invalid toHexChar char '$curChar' in unicode escape")
    }
}

private fun rangeEquals(source: String, start: Int, length: Int, str: String): Boolean {
    val n = str.length
    if (length != n) return false
    for (i in 0 until n) if (source[start + i] != str[i]) return false
    return true
}

internal inline fun require(condition: Boolean, pos: Int, msg: () -> String) {
    if (!condition)
        fail(pos, msg())
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun fail(pos: Int, msg: String): Nothing {
    throw IllegalArgumentException("JSON at $pos: $msg")
}

