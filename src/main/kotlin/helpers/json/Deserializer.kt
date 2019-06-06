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
data class ValueNode(val value: Any?) : Node()
data class ObjectNode(val props: ArrayList<Pair<String, Any?>>) : Node()
data class ArrayNode(val elements: ArrayList<Node>) : Node()

val postValueTokens = charArrayOf(comma, rightBracket, rightBrace)

class Parser(json: String) : Closeable {
    val reader = PushbackReader(json.reader())

    fun parse(
        props: ArrayList<Pair<String, Any?>> = arrayListOf(),
        elements: ArrayList<Node> = arrayListOf()
    ): Node {

        when (val currentChar = currentChar()) {
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
            doubleQuote -> {
                return ValueNode(readUntil(doubleQuote).first)
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
            in ('0'..'9') -> {
                val number = readUntil(*postValueTokens)
                unread(number.second)
                return ValueNode((currentChar + number.first).toDouble())
            }
            'n' -> return valueNode("null", null)
            'f' -> return valueNode("false", false)
            't' -> return valueNode("true", true)
        }
        return EmptyNode
    }

    private fun parseObject(
        props: ArrayList<Pair<String, Any?>> = arrayListOf(),
        elements: ArrayList<Node> = arrayListOf()
    ): Node {

        readUntil(doubleQuote)
        val key = readUntil(doubleQuote).first
        readUntil(colon)

        when (val currentChar = currentChar()) {
            doubleQuote -> {
                val value = readUntil(doubleQuote).first
                props += Pair(key, value)
                return parse(props, elements)
            }
            leftBrace -> {
                unread(currentChar)
                props += Pair(key, parse(elements = elements))
                return parse(props, elements)
            }
            leftBracket -> {
                elements += parse(props)
                props += Pair(key, parse(elements = elements))
                return parse(props)
            }
            in ('0'..'9') -> {
                val number = readUntil(*postValueTokens)
                unread(number.second)
                props += Pair(key, (currentChar + number.first).toDouble())
                return parse(props, elements)
            }
            'n' -> {
                props += Pair(key, objectValue("null", null))
                return parse(props, elements)
            }
            'f' -> {
                props += Pair(key, objectValue("false", false))
                return parse(props, elements)
            }
            't' -> {
                props += Pair(key, objectValue("true", true))
                return parse(props, elements)
            }
        }
        return EmptyNode
    }

    private fun currentChar(): Char {
        skip { it.isWhitespace() }
        return reader.read().toChar()
    }

    private fun unread(char: Char) = reader.unread(char.toInt())

    fun readUntil(vararg delimiters: Char): Pair<String, Char> {
        var char = '\u0000'
        return readUntil {
            char = it
            it in delimiters
        } to char
    }

    private fun readUntil(stopFn: (Char) -> Boolean): String {
        val out = StringWriter()
        var char = reader.read()
        while (char > 0 && !stopFn(char.toChar())) {
            out.write(char)
            char = reader.read()
        }
        return out.toString()
    }

    fun skip(continueFn: (Char) -> Boolean) {
        var char = reader.read()
        while (char > 0 && continueFn(char.toChar())) {
            char = reader.read()
        }
        reader.unread(char)
    }

    private fun objectValue(_valueToCheck: String, value: Any?): Any? {
        val valueToCheck = _valueToCheck.drop(1)
        val charArr = CharArray(valueToCheck.length)
        reader.read(charArr)

        if (charArr.contentEquals(valueToCheck.toCharArray())) {
            return value
        } else
            throw Exception("invalid constant value: $valueToCheck")
    }

    private fun valueNode(_valueToCheck: String, value: Any?): ValueNode {
        val valueToCheck = _valueToCheck.drop(1)
        val charArr = CharArray(valueToCheck.length)
        reader.read(charArr)

        if (charArr.contentEquals(valueToCheck.toCharArray())) {
            return ValueNode(value)
        }
        throw Exception("invalid constant value: $valueToCheck")
    }

    override fun close() {
        reader.close()
    }
}

fun main() {
    //{"age": true}
    val json = """
        {"menu": {
          "id": "file",
          "value": "File",
          "popup": {
            "menuitem": [
              {"value": "New", "onclick": "CreateNewDoc()"},
              {"value": "Open", "onclick": "OpenDoc()"},
              {"value": "Close", "onclick": "CloseDoc()"}
            ]
          }
        }}

        """

    Parser(json).use {
        val result = it.parse()
        println(result)
        //ObjectNode(props=[(menu, ObjectNode(props=[(id, file), (value, File), (popup, ObjectNode(props=[(menuitem, ArrayNode(elements=[ObjectNode(props=[(value, New), (onclick, CreateNewDoc())]), ObjectNode(props=[(value, Open), (onclick, OpenDoc())]), ObjectNode(props=[(value, Close), (onclick, CloseDoc())])]))]))]))])
    }
}