package heplers.json

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

class Tokenizer(json: String) {

    val reader = json.reader()

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

    fun close() {
        reader.close()
    }
}