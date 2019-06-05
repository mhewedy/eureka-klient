package heplers.json

import heplers.json.Token.colon
import heplers.json.Token.comma
import heplers.json.Token.doubleQuote
import heplers.json.Token.leftBrace
import heplers.json.Token.rightBrace
import java.io.PushbackReader
import java.io.StringWriter

// 1: writing tokenizer
// 2: building AST
// 3: convert AST to object graph

private object Token {
    const val leftBracket = '['
    const val rightBracket = ']'
    const val leftBrace = '{'
    const val rightBrace = '}'
    const val doubleQuote = '"'
    const val comma = ','
    const val colon = ':'
    const val space = ' '
}

sealed class Node
class EmptyNode : Node()
data class ObjectNode(val kvPairs: ArrayList<Pair<String, Any?>>) : Node()
data class ArrayNode(val elements: ObjectNode) : Node()

class Tokenizer(json: String) {
    val reader = PushbackReader(json.reader())

    fun parse(kvPairs: ArrayList<Pair<String, Any?>> = arrayListOf()): Node {
        fun currentChar() = reader.read().toChar()
        fun unread(char: Char) = reader.unread(char.toInt())

        when (currentChar()) {
            leftBrace -> {
                readUntil(doubleQuote)
                val key = readUntil(doubleQuote)
                readUntil(colon)
                skipWhiteSpace()
                when (val nextChar = currentChar()) {
                    doubleQuote -> {
                        val value = readUntil(doubleQuote)
                        kvPairs += Pair(key, value)
                        return parse(kvPairs)
                    }
                    leftBrace -> {
                        unread(nextChar)
                        kvPairs += Pair(key, parse())
                        return ObjectNode(kvPairs)
                    }
                }
            }
            rightBrace -> {
                return ObjectNode(kvPairs)
            }
            comma -> {
                readUntil(doubleQuote)
                val key = readUntil(doubleQuote)
                readUntil(colon)
                readUntil(doubleQuote)
                val value = readUntil(doubleQuote)
                kvPairs += Pair(key, value)
                return parse(kvPairs)
            }

        }
        return EmptyNode()
    }

    fun readUntil(delimiter: Char): String {
        return readUntil { it == delimiter }
    }

    fun readUntil(stopFn: (Char) -> Boolean): String {
        val out = StringWriter()
        var char = reader.read()
        while (char > 0 && !stopFn(char.toChar())) {
            out.write(char)
            char = reader.read()
        }
        return out.toString()
    }

    fun skipWhiteSpace() {
        skip { it.isWhitespace() }
    }

    fun skip(continueFn: (Char) -> Boolean) {
        var char = reader.read()
        while (char > 0 && continueFn(char.toChar())) {
            char = reader.read()
        }
        reader.unread(char)
    }

    fun close() {
        reader.close()
    }
}

fun main() {
    val json = """
        {"name":{"firstName":"wael"}}
    """.trimIndent()

    val tokenizer = Tokenizer(json)

    val node = tokenizer.parse()
    println(node)

    tokenizer.close()
}