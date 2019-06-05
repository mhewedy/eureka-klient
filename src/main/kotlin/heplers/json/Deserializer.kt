package heplers.json

import heplers.json.Token.colon
import heplers.json.Token.comma
import heplers.json.Token.doubleQuote
import heplers.json.Token.leftBrace
import heplers.json.Token.leftBracket
import heplers.json.Token.rightBrace
import heplers.json.Token.rightBracket
import java.io.Closeable
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
}

sealed class Node
object EmptyNode : Node()
data class ObjectNode(val props: ArrayList<Pair<String, Any?>>) : Node()
data class ArrayNode(val elements: ArrayList<Node>) : Node()

class Tokenizer(json: String) : Closeable {
    val reader = PushbackReader(json.reader())

    private fun currentChar() = reader.read().toChar()
    private fun unread(char: Char) = reader.unread(char.toInt())

    fun parse(
        props: ArrayList<Pair<String, Any?>> = arrayListOf(),
        elements: ArrayList<Node> = arrayListOf()
    ): Node {

        when (currentChar()) {
            leftBrace -> {
                return parseObject(props)
            }
            rightBrace -> {
                return ObjectNode(props)
            }
            leftBracket -> {
                return parseArray(elements)
            }
            rightBracket -> {
                return ArrayNode(elements)
            }
            comma -> {
                if (props.isNotEmpty())
                    return parseObject(props)
                if (elements.isNotEmpty())
                    return parseArray(elements)
            }
        }
        return EmptyNode
    }

    private fun parseArray(elements: ArrayList<Node>): Node {
        elements += parse()
        return parse(elements = elements)
    }

    private fun parseObject(props: ArrayList<Pair<String, Any?>>): Node {
        readUntil(doubleQuote)
        val key = readUntil(doubleQuote)
        readUntil(colon)
        skipWhiteSpace()
        when (val nextChar = currentChar()) {
            doubleQuote -> {
                val value = readUntil(doubleQuote)
                props += Pair(key, value)
                return parse(props)
            }
            leftBrace -> {
                unread(nextChar)
                props += Pair(key, parse())
                return parse(props)
            }
            else -> EmptyNode
        }
        return EmptyNode
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

    override fun close() {
        reader.close()
    }
}

fun main() {
    val json = """
        [[[{"name":"abc"},{"age":"efg"}]]]
    """.trimIndent()

    Tokenizer(json).use {
        println(it.parse())
    }
}