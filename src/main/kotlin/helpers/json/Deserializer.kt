package helpers.json

import helpers.json.Token.colon
import helpers.json.Token.comma
import helpers.json.Token.doubleQuote
import helpers.json.Token.leftBrace
import helpers.json.Token.leftBracket
import helpers.json.Token.rightBrace
import helpers.json.Token.rightBracket
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

class Parser(json: String) : Closeable {
    val reader = PushbackReader(json.reader())

    private fun currentChar() = reader.read().toChar()
    private fun unread(char: Char) = reader.unread(char.toInt())

    fun parse(
        props: ArrayList<Pair<String, Any?>> = arrayListOf(),
        elements: ArrayList<Node> = arrayListOf()
    ): Node {

        when (currentChar()) {
            leftBrace -> {
                return parseObject(elements = elements)
            }
            rightBrace -> {
                return ObjectNode(props)
            }
            leftBracket -> {
                elements += parse()
                return parse(elements = elements)
            }
            rightBracket -> {
                return ArrayNode(elements)
            }
            comma -> {
                if (elements.isNotEmpty()) {
                    elements += parse(props)
                    return parse(props, elements)
                }
                if (props.isNotEmpty()) {
                    return parseObject(props, elements)
                }
            }
        }
        return EmptyNode
    }

    private fun parseObject(
        props: ArrayList<Pair<String, Any?>> = arrayListOf(),
        elements: ArrayList<Node> = arrayListOf()
    ): Node {
        readUntil(doubleQuote)
        val key = readUntil(doubleQuote)
        readUntil(colon)
        skipWhiteSpaces()
        when (val nextChar = currentChar()) {
            doubleQuote -> {
                val value = readUntil(doubleQuote)
                props += Pair(key, value)
                return parse(props, elements)
            }
            leftBrace -> {
                unread(nextChar)
                props += Pair(key, parse(elements = elements))
                return parse(props, elements)
            }
            leftBracket -> {
                elements += parse(props)
                props += Pair(key, parse(elements = elements))
                return parse(props, elements)
            }
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

    fun skipWhiteSpaces() {
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
    //[[[{"name":"efg"},{"name":"efg"}]]]
    val json = """
        {"type":"1","model":[{"years":[{"name":"elephant","digits":"2019"},{"name":"joungle","digits":"2018"}]},{"years":[{"name":"donkey","digits":"2015"},{"name":"tiger","digits":"1009"}]}]}
    """.trimIndent()

    Parser(json).use {
        val result = it.parse()
        println(result)
    }
}